package cl.duoc.m

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.test.platform.app.InstrumentationRegistry
import cl.duoc.m.repository.MediaFiles
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class MediaValidationTest {
    private val context=InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun importedImageIsResizedAndSavedPrivatelyAndInvalidFilesAreRejected() {
        val source=File.createTempFile("validation_",".png",context.cacheDir)
        var preview: File?=null
        var saved: File?=null
        try {
            val bitmap=Bitmap.createBitmap(2200,1200,Bitmap.Config.ARGB_8888)
            source.outputStream().use {bitmap.compress(Bitmap.CompressFormat.PNG,100,it)}
            bitmap.recycle()
            preview=File(MediaFiles.candidate(context,Uri.fromFile(source)))
            saved=File(MediaFiles.save(context,3,preview.path))
            assertTrue(saved.exists())
            val decoded=requireNotNull(MediaFiles.decode(saved.path))
            assertTrue(decoded.width<=1024 && decoded.height<=1024)
            decoded.recycle()
            assertTrue(saved.canonicalPath.startsWith(context.filesDir.canonicalPath+File.separator))
            source.writeText("Esto no es una imagen")
            assertNull(MediaFiles.decode(source.path))
            try {MediaFiles.candidate(context,Uri.fromFile(source));fail("Debe rechazar el archivo inválido")} catch(_: IllegalStateException) {}
            try {MediaFiles.save(context,3,saved.path);fail("Debe rechazar una ruta fuera de caché")} catch(_: IllegalArgumentException) {}
        } finally {source.delete();preview?.delete();saved?.delete()}
    }

    @Test fun cameraProviderCanWriteAndReadACaptureAndAudioCanStart() {
        val directory=File(context.cacheDir,"camera").apply {mkdirs()}
        val capture=File.createTempFile("validation_",".jpg",directory)
        try {
            val uri=FileProvider.getUriForFile(context,"${context.packageName}.files",capture)
            val bitmap=Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888)
            context.contentResolver.openOutputStream(uri)!!.use {bitmap.compress(Bitmap.CompressFormat.JPEG,88,it)}
            bitmap.recycle()
            val decoded=requireNotNull(MediaFiles.decode(capture.path))
            assertEquals(100,decoded.width)
            decoded.recycle()
            val player=requireNotNull(MediaPlayer.create(context,R.raw.instrucciones))
            try {assertTrue(player.duration>0);player.start();assertTrue(player.isPlaying);player.stop()} finally {player.release()}
        } finally {capture.delete()}
    }
}
