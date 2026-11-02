package cl.duoc.m.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cl.duoc.m.model.*
import cl.duoc.m.ui.theme.MPAZPRO_GrupoTheme

private val previewUser=User(3,"estudiante","Estudiante Sol",Role.ESTUDIANTE,"2.º Básico A",2)
private val previewLessons=listOf("Lenguaje","Matemática","Ciencias Naturales","Historia y Geografía").mapIndexed {i,subject -> Lesson(i.toLong(),subject,"Explora y aprende","Objetivo de demostración","Texto breve","Ejemplo",UnitStatus.PUBLICADO)}
@Preview(name="Compacto",widthDp=360,heightDp=800,showBackground=true)
@Composable private fun CompactPreview() {MPAZPRO_GrupoTheme {HomeScreen(previewUser,previewLessons,1,{},{})}}
@Preview(name="Medio",widthDp=760,heightDp=900,showBackground=true)
@Composable private fun MediumPreview() {MPAZPRO_GrupoTheme {HomeScreen(previewUser,previewLessons,2,{},{})}}
@Preview(name="Expandido",widthDp=1100,heightDp=900,showBackground=true)
@Composable private fun ExpandedPreview() {MPAZPRO_GrupoTheme {HomeScreen(previewUser,previewLessons,3,{},{})}}
