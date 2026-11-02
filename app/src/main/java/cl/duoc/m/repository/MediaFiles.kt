package cl.duoc.m.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

// Complemento: una URI de galería puede ser temporal. Guardamos una copia JPEG propia.
object MediaFiles {
    fun candidate(context: Context,uri: Uri): String {
        val temp=File.createTempFile("selected_",".image",context.cacheDir)
        try {
            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) { "No pudimos abrir la imagen" }
                temp.outputStream().use { output ->
                    val buffer=ByteArray(8192);var total=0
                    while(true) {val count=input.read(buffer);if(count<0) break;total+=count;require(total<=16*1024*1024) {"Selecciona una imagen menor a 16 MB"};output.write(buffer,0,count)}
                }
            }
            val bitmap=decode(temp.path) ?: error("El archivo no es una imagen compatible")
            val output=File.createTempFile("preview_",".jpg",context.cacheDir)
            output.outputStream().use {bitmap.compress(Bitmap.CompressFormat.JPEG,88,it)}
            bitmap.recycle()
            return output.path
        } finally { temp.delete() }
    }
    fun decode(path: String): Bitmap? {
        val bounds=BitmapFactory.Options().apply {inJustDecodeBounds=true}
        BitmapFactory.decodeFile(path,bounds)
        if(bounds.outWidth<=0 || bounds.outHeight<=0) return null
        var sample=1
        while(bounds.outWidth/sample>1024 || bounds.outHeight/sample>1024) sample*=2
        return BitmapFactory.decodeFile(path,BitmapFactory.Options().apply {inSampleSize=sample})
    }
    fun save(context: Context,studentId: Long,candidate: String): String {
        require(File(candidate).canonicalPath.startsWith(context.cacheDir.canonicalPath+File.separator)) {"Imagen temporal no válida"}
        val bitmap=decode(candidate) ?: error("Toma o selecciona una imagen antes de guardar")
        val dir=File(context.filesDir,"avatars").apply {mkdirs()}
        val target=File(dir,"student_${studentId}_${System.currentTimeMillis()}.jpg")
        target.outputStream().use {bitmap.compress(Bitmap.CompressFormat.JPEG,88,it)}
        bitmap.recycle()
        return target.path
    }
}
