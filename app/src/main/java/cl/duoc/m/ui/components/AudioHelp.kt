package cl.duoc.m.ui.components

import android.media.MediaPlayer
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import cl.duoc.m.R

// Audio empaquetado: funciona sin internet y sin solicitar acceso al micrófono.
@Composable
fun AudioHelp() {
    val context=LocalContext.current
    var player by remember {mutableStateOf<MediaPlayer?>(null)}
    var playing by remember {mutableStateOf(false)}
    var failed by remember {mutableStateOf(false)}
    DisposableEffect(Unit) {onDispose {player?.release()}}
    Action(if(playing) "Detener instrucciones de audio" else "Escuchar instrucciones") {
        if(playing) {player?.release();player=null;playing=false}
        else try {
            player=MediaPlayer.create(context,R.raw.instrucciones)?.apply {
                setOnCompletionListener {it.release();player=null;playing=false}
                setOnErrorListener {p,_,_ -> p.release();player=null;playing=false;failed=true;true}
                start()
            }
            playing=player!=null;failed=player==null
        } catch (_: Exception) {failed=true;playing=false}
    }
    if(failed) Notice("No pudimos reproducir el audio. Lee las instrucciones o pide apoyo al docente.",true)
}
