package com.example.e_xamify;
    import android.content.ContentValues;
    import android.content.Context;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
 
    public class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "examify.db";
        private static final int DATABASE_VERSION = 1;
 
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
 
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Create tables
            db.execSQL("CREATE TABLE user (user_id INTEGER PRIMARY KEY, user_email TEXT, user_password TEXT, user_name TEXT, user_role_id INTEGER, joined_date TEXT)");
            db.execSQL("CREATE TABLE teacher (user_id INTEGER PRIMARY KEY, teacher_name TEXT, teacher_field TEXT, teacher_joined_date TEXT, teacher_img_url TEXT)");
            db.execSQL("CREATE TABLE student (user_id INTEGER PRIMARY KEY, student_name TEXT, student_img_url TEXT)");
            db.execSQL("CREATE TABLE institution (user_id INTEGER PRIMARY KEY, institution_name TEXT, institution_phone TEXT, institution_address TEXT, institution_enrolment_key TEXT, institution_date_joined TEXT)");
            db.execSQL("CREATE TABLE teacher_institution (teacher_enrolment_ID INTEGER PRIMARY KEY, user_id INTEGER, institution_enrolment_key TEXT)");
            db.execSQL("CREATE TABLE student_institution (student_enrolment_ID INTEGER PRIMARY KEY, user_id INTEGER, institution_enrolment_key TEXT)");
            db.execSQL("CREATE TABLE quiz (quiz_id INTEGER PRIMARY KEY, quiz_type_id INTEGER, quiz_title TEXT, quiz_duration INTEGER, instructions TEXT, quiz_attempts INTEGER, user_id INTEGER, quiz_navigable INTEGER, quiz_tab_restrictor INTEGER, question_randomize INTEGER)");
            db.execSQL("CREATE TABLE question (question_id INTEGER PRIMARY KEY, quiz_id INTEGER, question_text TEXT, question_type_id INTEGER, question_img_url TEXT)");
            db.execSQL("CREATE TABLE question_type (question_type_id INTEGER PRIMARY KEY, type_name TEXT, type_description TEXT)");
            db.execSQL("CREATE TABLE mcq (option_id INTEGER PRIMARY KEY, question_id INTEGER, option_text TEXT, is_correct INTEGER)");
            db.execSQL("CREATE TABLE assignment (assignment_id INTEGER PRIMARY KEY, quiz_id INTEGER, user_id INTEGER, status TEXT, attempt_number_left INTEGER, mark INTEGER, assignment_start_date TEXT, assignment_end_date TEXT)");
            db.execSQL("CREATE TABLE feedback (feedback_id INTEGER PRIMARY KEY, user_id INTEGER, assignment_id INTEGER, feedback_text TEXT, is_visible INTEGER)");
            db.execSQL("CREATE TABLE user_role (user_role_id INTEGER PRIMARY KEY, role_name TEXT, role_description TEXT)");
        }
 
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Handle database upgrade as needed
            db.execSQL("DROP TABLE IF EXISTS user");
            db.execSQL("DROP TABLE IF EXISTS teacher");
            db.execSQL("DROP TABLE IF EXISTS student");
            db.execSQL("DROP TABLE IF EXISTS institution");
            db.execSQL("DROP TABLE IF EXISTS teacher_institution");
            db.execSQL("DROP TABLE IF EXISTS student_institution");
            db.execSQL("DROP TABLE IF EXISTS quiz");
            db.execSQL("DROP TABLE IF EXISTS question");
            db.execSQL("DROP TABLE IF EXISTS question_type");
            db.execSQL("DROP TABLE IF EXISTS mcq");
            db.execSQL("DROP TABLE IF EXISTS assignment");
            db.execSQL("DROP TABLE IF EXISTS feedback");
            db.execSQL("DROP TABLE IF EXISTS user_role");
            onCreate(db);
        }
        public void addStudent(String email, String password, String name, int role_id, String joined_date) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_email", email);
            values.put("user_password", password);
            
        }
    }

