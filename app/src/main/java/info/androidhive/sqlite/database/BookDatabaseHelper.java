package info.androidhive.sqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.sqlite.database.model.Book;


public class BookDatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "books_db";


    public BookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create books table
        db.execSQL(Book.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Book.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public int addBook(Book book) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Book.COLUMN_TITLE, book.getTitle());
        values.put(Book.COLUMN_AUTHOR, book.getAuthor());

        // insert row
        long id = db.insert(Book.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return (int)id;
    }

    public Book getBook(int id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Book.TABLE_NAME,
                new String[]{Book.COLUMN_ID, Book.COLUMN_TITLE, Book.COLUMN_AUTHOR},
                Book.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare book object
        Book book = new Book(
                cursor.getInt(cursor.getColumnIndex(Book.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Book.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(Book.COLUMN_AUTHOR)));

        // close the db connection
        cursor.close();

        return book;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Book.TABLE_NAME + " ORDER BY " +
                Book.COLUMN_TITLE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_TITLE)));
                book.setAuthor(cursor.getString(cursor.getColumnIndex(Book.COLUMN_AUTHOR)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return books list
        return books;
    }

    public int getBooksCount() {
        String countQuery = "SELECT  * FROM " + Book.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Book.COLUMN_TITLE, book.getTitle());

        // updating row
        return db.update(Book.TABLE_NAME, values, Book.COLUMN_ID + " = ?",
                new String[]{String.valueOf(book.getId())});
    }

    public void deleteBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Book.TABLE_NAME, Book.COLUMN_ID + " = ?",
                new String[]{String.valueOf(book.getId())});
        db.close();
    }
}
