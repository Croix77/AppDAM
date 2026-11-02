package cl.duoc.m.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest
import java.security.SecureRandom

// Complemento a las guías: SQLiteOpenHelper crea la base local una sola vez.
class SchoolDatabase(context: Context, name: String = "mpaz_school.db") : SQLiteOpenHelper(context, name, null, 2) {
    override fun onConfigure(db: SQLiteDatabase) { db.setForeignKeyConstraintsEnabled(true) }
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY, username TEXT UNIQUE NOT NULL, name TEXT NOT NULL, role TEXT NOT NULL, course TEXT NOT NULL, teacher_id INTEGER REFERENCES users(id), salt TEXT NOT NULL, password TEXT NOT NULL, photo TEXT NOT NULL DEFAULT '')")
        db.execSQL("CREATE TABLE lessons(id INTEGER PRIMARY KEY AUTOINCREMENT, subject TEXT NOT NULL, title TEXT NOT NULL, objective TEXT NOT NULL, explanation TEXT NOT NULL, example TEXT NOT NULL, status TEXT NOT NULL, origin TEXT NOT NULL)")
        db.execSQL("CREATE TABLE questions(id INTEGER PRIMARY KEY AUTOINCREMENT, lesson_id INTEGER NOT NULL REFERENCES lessons(id), prompt TEXT NOT NULL, a TEXT NOT NULL, b TEXT NOT NULL, c TEXT NOT NULL, correct INTEGER NOT NULL CHECK(correct BETWEEN 0 AND 2), explanation TEXT NOT NULL, final_test INTEGER NOT NULL, kind TEXT NOT NULL DEFAULT 'SELECCION')")
        db.execSQL("CREATE TABLE assignments(student_id INTEGER REFERENCES users(id), lesson_id INTEGER REFERENCES lessons(id), max_attempts INTEGER NOT NULL CHECK(max_attempts BETWEEN 1 AND 5), start_date TEXT NOT NULL, end_date TEXT NOT NULL, PRIMARY KEY(student_id,lesson_id))")
        db.execSQL("CREATE TABLE practice(student_id INTEGER REFERENCES users(id), question_id INTEGER REFERENCES questions(id), choice INTEGER NOT NULL, correct INTEGER NOT NULL, date TEXT NOT NULL, PRIMARY KEY(student_id,question_id))")
        db.execSQL("CREATE TABLE attempts(id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER REFERENCES users(id), lesson_id INTEGER REFERENCES lessons(id), number INTEGER NOT NULL, score INTEGER NOT NULL, total INTEGER NOT NULL, date TEXT NOT NULL, UNIQUE(student_id,lesson_id,number))")
        db.execSQL("CREATE TABLE test_answers(attempt_id INTEGER REFERENCES attempts(id), question_id INTEGER REFERENCES questions(id), choice INTEGER NOT NULL, correct INTEGER NOT NULL, PRIMARY KEY(attempt_id,question_id))")
        db.execSQL("CREATE TABLE notes(student_id INTEGER REFERENCES users(id), lesson_id INTEGER REFERENCES lessons(id), note TEXT NOT NULL, validated INTEGER NOT NULL, PRIMARY KEY(student_id,lesson_id))")
        db.execSQL("CREATE TABLE audit(id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, actor TEXT NOT NULL, action TEXT NOT NULL)")
        val accounts = listOf(
            arrayOf("1", "utp", "Coordinación de prueba", "UTP", "Equipo pedagógico", "", "Utp12345"),
            arrayOf("2", "docente", "Docente de prueba", "DOCENTE", "2.º Básico A", "", "Profe123"),
            arrayOf("3", "estudiante", "Estudiante Sol", "ESTUDIANTE", "2.º Básico A", "2", "Aprendo123"),
            arrayOf("4", "luna", "Estudiante Luna", "ESTUDIANTE", "2.º Básico A", "2", "Aprendo123")
        )
        accounts.forEach { u ->
            val salt = Passwords.salt()
            db.execSQL("INSERT INTO users(id,username,name,role,course,teacher_id,salt,password) VALUES(?,?,?,?,?,?,?,?)", arrayOf(u[0],u[1],u[2],u[3],u[4],u[5].toLongOrNull(),salt,Passwords.hash(u[6],salt)))
        }
        SeedContent.insert(db)
        SubjectActivities.insert(db)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Se agregan columnas/unidades; no se eliminan tablas ni respuestas anteriores.
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE questions ADD COLUMN kind TEXT NOT NULL DEFAULT 'SELECCION'")
            SubjectActivities.insert(db)
        }
    }
}

object Passwords {
    fun salt(): String = ByteArray(16).also { SecureRandom().nextBytes(it) }.joinToString("") { "%02x".format(it) }
    fun hash(password: String, salt: String): String {
        val text = (salt + password).toByteArray()
        return MessageDigest.getInstance("SHA-256").digest(text).joinToString("") { "%02x".format(it) }
    }
    fun matches(password: String, salt: String, expected: String): Boolean = hash(password, salt) == expected
}
