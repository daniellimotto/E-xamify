package com.example.e_xamify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "examify.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void seedDatabase(SQLiteDatabase db) {
        db.execSQL("INSERT INTO quiz_type (quiz_type_id, type_name, type_description) VALUES (1, 'Practice', 'Non-graded practice quiz')");
        db.execSQL("INSERT INTO quiz_type (quiz_type_id, type_name, type_description) VALUES (2, 'Coursework', 'Daily graded coursework')");
        db.execSQL("INSERT INTO quiz_type (quiz_type_id, type_name, type_description) VALUES (3, 'Exam', 'Graded exam')");


        db.execSQL("INSERT INTO user_role (user_role_id, role_name, role_description) VALUES (1, 'Institution', 'Roles identification for institutions')");
        db.execSQL("INSERT INTO user_role (user_role_id, role_name, role_description) VALUES (2, 'Teacher', 'Roles identification for teachers')");
        db.execSQL("INSERT INTO user_role (user_role_id, role_name, role_description) VALUES (3, 'Student', 'Roles identification for students')");

        db.execSQL("INSERT INTO question_type (question_type_id, type_name, type_description) VALUES (1, 'MCQ', 'Multiple Choice Question')");

        }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL("CREATE TABLE user_role (user_role_id INTEGER PRIMARY KEY, role_name TEXT, role_description TEXT)");
        db.execSQL("CREATE TABLE user (user_id INTEGER PRIMARY KEY, user_email TEXT, user_password TEXT, user_name TEXT, user_role_id INTEGER, joined_date TEXT, FOREIGN KEY(user_role_id) REFERENCES user_role(user_role_id))");
        db.execSQL("CREATE TABLE institution (user_id INTEGER PRIMARY KEY, institution_name TEXT, institution_phone TEXT, institution_address TEXT, institution_enrolment_key TEXT UNIQUE, institution_date_joined TEXT, FOREIGN KEY(user_id) REFERENCES user(user_id))");
        db.execSQL("CREATE TABLE module (module_id INTEGER PRIMARY KEY, institution_id INTEGER, module_name TEXT, module_description TEXT, module_key TEXT UNIQUE, FOREIGN KEY(institution_id) REFERENCES institution(user_id))");
        db.execSQL("CREATE TABLE quiz_type (quiz_type_id INTEGER PRIMARY KEY, type_name TEXT, type_description TEXT)");
        db.execSQL("CREATE TABLE question_type (question_type_id INTEGER PRIMARY KEY, type_name TEXT, type_description TEXT)");
        db.execSQL("CREATE TABLE student (user_id INTEGER PRIMARY KEY, student_name TEXT, student_img_url TEXT, FOREIGN KEY(user_id) REFERENCES user(user_id))");
        db.execSQL("CREATE TABLE teacher (user_id INTEGER PRIMARY KEY, teacher_name TEXT, teacher_field TEXT, teacher_joined_date TEXT, teacher_img_url TEXT, FOREIGN KEY(user_id) REFERENCES user(user_id))");
        db.execSQL("CREATE TABLE question (question_id INTEGER PRIMARY KEY AUTOINCREMENT, question_number INTEGER, quiz_id INTEGER, question_text TEXT, question_type_id INTEGER, question_img_url TEXT, FOREIGN KEY(quiz_id) REFERENCES quiz(quiz_id), FOREIGN KEY(question_type_id) REFERENCES question_type(question_type_id),  UNIQUE(quiz_id, question_number)) ");
        db.execSQL("CREATE TABLE mcq (option_id INTEGER PRIMARY KEY AUTOINCREMENT, question_id INTEGER, optionA TEXT, optionB TEXT, optionC TEXT, optionD TEXT, correctOption INTEGER, FOREIGN KEY(question_id) REFERENCES question(question_id))");
        db.execSQL("CREATE TABLE assignment (assignment_id INTEGER PRIMARY KEY, quiz_id INTEGER, user_id INTEGER, status TEXT, attempt_number_left INTEGER, mark INTEGER, assignment_start_date TEXT, assignment_end_date TEXT, quiz_title TEXT, FOREIGN KEY(quiz_id) REFERENCES quiz(quiz_id), FOREIGN KEY(user_id) REFERENCES student(user_id))");
        db.execSQL("CREATE TABLE feedback (feedback_id INTEGER PRIMARY KEY, user_id INTEGER, assignment_id INTEGER, feedback_text TEXT, is_visible INTEGER, FOREIGN KEY(user_id) REFERENCES teacher(user_id), FOREIGN KEY(assignment_id) REFERENCES assignment(assignment_id))");
        db.execSQL("CREATE TABLE quiz_submission (submission_id INTEGER PRIMARY KEY, assignment_id INTEGER, question_id INTEGER, user_id INTEGER, selected_option_id INTEGER, answer_text TEXT, is_correct INTEGER, submission_date TEXT, FOREIGN KEY(assignment_id) REFERENCES assignment(assignment_id), FOREIGN KEY(question_id) REFERENCES question(question_id), FOREIGN KEY(user_id) REFERENCES student(user_id))");
        db.execSQL("CREATE TABLE student_module (student_module_ID INTEGER PRIMARY KEY, user_id INTEGER, module_id INTEGER, enrollment_date TEXT, FOREIGN KEY(user_id) REFERENCES student(user_id), FOREIGN KEY(module_id) REFERENCES module(module_id))");
        db.execSQL("CREATE TABLE student_institution (student_institution_id INTEGER PRIMARY KEY, student_id INTEGER NOT NULL, institution_id INTEGER NOT NULL, enrollment_date TEXT, FOREIGN KEY(student_id) REFERENCES student(user_id), FOREIGN KEY(institution_id) REFERENCES institution(user_id))");
        db.execSQL("CREATE TABLE teacher_institution (teacher_institution_id INTEGER PRIMARY KEY, teacher_id INTEGER NOT NULL, institution_id INTEGER NOT NULL, enrollment_date TEXT, FOREIGN KEY(teacher_id) REFERENCES teacher(user_id), FOREIGN KEY(institution_id) REFERENCES institution(user_id))");
        db.execSQL("CREATE TABLE quiz (quiz_id INTEGER PRIMARY KEY AUTOINCREMENT, quiz_type_id INTEGER, quiz_title TEXT, quiz_duration INTEGER, instructions TEXT, quiz_attempts INTEGER, user_id INTEGER, quiz_navigable INTEGER, quiz_tab_restrictor INTEGER, module_id INTEGER, num_questions INTEGER, FOREIGN KEY(quiz_type_id) REFERENCES quiz_type(quiz_type_id), FOREIGN KEY(user_id) REFERENCES teacher(user_id), FOREIGN KEY(module_id) REFERENCES module(module_id))");
        db.execSQL("CREATE TABLE quiz_attempt (attempt_id INTEGER PRIMARY KEY AUTOINCREMENT, quiz_id INTEGER, user_id INTEGER, start_time TEXT, end_time TEXT, score INTEGER, status TEXT, FOREIGN KEY(quiz_id) REFERENCES quiz(quiz_id), FOREIGN KEY(user_id) REFERENCES user(user_id))");
        seedDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user_role");
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS institution");
        db.execSQL("DROP TABLE IF EXISTS module");
        db.execSQL("DROP TABLE IF EXISTS quiz_type");
        db.execSQL("DROP TABLE IF EXISTS quiz");
        db.execSQL("DROP TABLE IF EXISTS question_type");
        db.execSQL("DROP TABLE IF EXISTS student");
        db.execSQL("DROP TABLE IF EXISTS teacher");
        db.execSQL("DROP TABLE IF EXISTS question");
        db.execSQL("DROP TABLE IF EXISTS mcq");
        db.execSQL("DROP TABLE IF EXISTS assignment");
        db.execSQL("DROP TABLE IF EXISTS feedback");
        db.execSQL("DROP TABLE IF EXISTS quiz_submission");
        db.execSQL("DROP TABLE IF EXISTS student_module");
        db.execSQL("DROP TABLE IF EXISTS student_institution");
        db.execSQL("DROP TABLE IF EXISTS teacher_institution");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    public boolean isTeacherEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_email FROM user WHERE user_email = ? AND user_role_id = 2", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean isStudentEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_email FROM user WHERE user_email = ? AND user_role_id = 3", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean isInstitutionEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_email FROM user WHERE user_email = ? AND user_role_id = 1", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}
