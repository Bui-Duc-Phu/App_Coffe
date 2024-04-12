package com.example.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
	SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

	override fun onCreate(db: SQLiteDatabase) {

		val query = ("CREATE TABLE " + TABLE_NAME + " ("
				+ ID_COL + " INTEGER PRIMARY KEY, " +
				MODE + " TEXT" + ")")

		db.execSQL(query)


	}





	override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
		onCreate(db)
	}


	fun addName(id : String, mode : String ){
		val values = ContentValues()
		values.put(ID_COL, id)
		values.put(MODE, mode)
		val db = this.writableDatabase
		db.insert(TABLE_NAME, null, values)
		db.close()
	}



	fun getModeList(): List<String> {
		val modeList = mutableListOf<String>()
		val db = this.readableDatabase
		val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
		cursor?.use {
			while (cursor.moveToNext()) {
				val mode = cursor.getString(cursor.getColumnIndexOrThrow(MODE))
				modeList.add(mode)
			}
		}
		cursor.close()
		db.close()
		return modeList
	}

	fun updateMode(id: String, newMode: String) {
		val db = this.writableDatabase
		val values = ContentValues().apply {
			put(MODE, newMode)
		}
		db.update(TABLE_NAME, values, "$ID_COL = ?", arrayOf(id))
		db.close()
	}


	companion object{

		private val DATABASE_NAME = "MODE_THEME_DATABASE"
		private val DATABASE_VERSION = 1
		val TABLE_NAME = "MODE"
		val ID_COL = "id"
		val MODE = "mode"

	}
}
