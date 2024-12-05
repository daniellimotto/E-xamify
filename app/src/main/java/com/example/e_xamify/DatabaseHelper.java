package com.example.e_xamify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "examify.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void seedDatabase(SQLiteDatabase db) {
        // Insert quiz types
        db.execSQL("INSERT INTO quiz_type (quiz_type_id, type_name, type_description) VALUES (1, 'Practice', 'Non-graded practice quiz')");
        db.execSQL("INSERT INTO quiz_type (quiz_type_id, type_name, type_description) VALUES (2, 'Coursework', 'Daily graded coursework')");
        db.execSQL("INSERT INTO quiz_type (quiz_type_id, type_name, type_description) VALUES (3, 'Exam', 'Graded exam')");

        db.execSQL("INSERT INTO module (module_id, module_name, module_description) VALUES (1, 'MTH001', 'Math basics')");
        db.execSQL("INSERT INTO module (module_id, module_name, module_description) VALUES (2, 'SCI001', 'Basic science concepts')");
        db.execSQL("INSERT INTO module (module_id, module_name, module_description) VALUES (3, 'CAN001', 'Mobile Computing')");
        db.execSQL("INSERT INTO module (module_id, module_name, module_description) VALUES (0, 'Others', 'Unclassified Module')");

        db.execSQL("INSERT INTO user_role (user_role_id, role_name, role_description) VALUES (1, 'Institution', 'Roles identification for institutions')");
        db.execSQL("INSERT INTO user_role (user_role_id, role_name, role_description) VALUES (2, 'Teacher', 'Roles identification for teachers')");
        db.execSQL("INSERT INTO user_role (user_role_id, role_name, role_description) VALUES (3, 'Student', 'Roles identification for students')");

        db.execSQL("INSERT INTO question_type (question_type_id, type_name, type_description) VALUES (1, 'MCQ', 'Multiple Choice Question')");

        // Add one Institution
        db.execSQL("INSERT INTO user (user_id, user_email, user_password, user_name, user_role_id, joined_date) " +
                "VALUES (1, 'admin@mit.edu', 'adminpass', 'MIT Admin', 1, '2023-01-01')");
        db.execSQL("INSERT INTO institution (user_id, institution_name, institution_phone, institution_address, institution_enrolment_key, institution_date_joined) " +
                "VALUES (1, 'Massachusetts Institute of Technology', '+1234567890', '77 Massachusetts Ave, Cambridge, MA 02139', 'MITKEY2023', '2023-01-01')");

        // Add one Teacher
        db.execSQL("INSERT INTO user (user_id, user_email, user_password, user_name, user_role_id, joined_date) " +
                "VALUES (2, 'teacher@mit.edu', 'teacherpass', 'Prof. John Smith', 2, '2023-06-15')");
        db.execSQL("INSERT INTO teacher (user_id, teacher_name, teacher_field, teacher_img_url) " +
                "VALUES (2, 'Prof. John Smith', 'Mathematics', NULL)");

        // Add one Student
        db.execSQL("INSERT INTO user (user_id, user_email, user_password, user_name, user_role_id, joined_date) " +
                "VALUES (3, 'student@mit.edu', 'studentpass', 'Alice Brown', 3, '2023-09-01')");
        db.execSQL("INSERT INTO student (user_id, student_name, student_img_url) " +
                "VALUES (3, 'Alice Brown', NULL)");

        //kenz seeder
        db.execSQL("INSERT INTO user (user_id, user_email, user_password, user_name, user_role_id, joined_date) " +
                "VALUES (4, '3', '3', '3', 3, '2024-11-22')");
        db.execSQL("INSERT INTO student (user_id, student_name, student_img_url) " +
                "VALUES (4, '3', NULL)");
        db.execSQL("INSERT INTO student_institution (student_institution_id, student_id, institution_id, enrollment_date) " +
                "VALUES (2, 4, 6, '2024-11-22')");
        db.execSQL("INSERT INTO user (user_id, user_email, user_password, user_name, user_role_id, joined_date) " +
                "VALUES (5, '5', '5', '5', 2, '2024-11-22')");
        db.execSQL("INSERT INTO teacher (user_id, teacher_name, teacher_field, teacher_img_url) " +
                "VALUES (5, '5', 'Mathematics', NULL)");
        db.execSQL("INSERT INTO teacher_institution (teacher_institution_id, teacher_id, institution_id, enrollment_date) " +
                "VALUES (2, 5, 6, '2024-11-22')");
        db.execSQL("INSERT INTO user (user_id, user_email, user_password, user_name, user_role_id, joined_date) " +
                "VALUES (6, '6', '6', '6', 1, '2024-11-22')");
        db.execSQL("INSERT INTO institution (user_id, institution_name, institution_phone, institution_address, institution_enrolment_key, institution_date_joined) " +
                "VALUES (6, 'university', '+1234567890', '77 Massachusetts Ave, Cambridge, MA 02139', 'WOWUNI', '2024-11-22')");
        db.execSQL("INSERT INTO module (module_id, institution_id, module_name, module_description, module_key) " +
                "VALUES (5, 6, 'Math101', 'Wow Desc', 'wow')");
        db.execSQL("INSERT INTO student_module (student_module_ID, user_id, module_id, enrollment_date)" +
                "VALUES (1, 4, 5, NULL)");
        db.execSQL("INSERT INTO quiz (quiz_id, quiz_type_id, quiz_title, quiz_duration, instructions, quiz_attempts, user_id, quiz_navigable, quiz_tab_restrictor, module_id, num_questions)" +
                "VALUES (2, 1, 'wowow', 60, 'The duration of this assignment is 60 minutes. The questions are not navigable and you are not allowed to switch between other tabs. Please fill in your answer before going to the next question.', 100, 5, 0, 0, 5, 3 )");
        db.execSQL("INSERT INTO assignment(assignment_id, quiz_id, user_id, status, attempt_number_left, mark, assignment_start_date, assignment_end_date, quiz_title)" +
                "VALUES (2, 2, 4, 'pending', 1, NULL, 2024, 2026, NULL)");
        db.execSQL("INSERT INTO mcq(option_id, question_id, optionA, optionB, optionC, optionD, correctOption)" +
                "VALUES (2, 2, 1, 2, 3, 4, 2)");
        db.execSQL("INSERT INTO mcq(option_id, question_id, optionA, optionB, optionC, optionD, correctOption)" +
                "VALUES (3, 3, 1, 2, 3, 4, 1)");
        db.execSQL("INSERT INTO mcq(option_id, question_id, optionA, optionB, optionC, optionD, correctOption)" +
                "VALUES (4, 4, 1, 2, 3, 4, 4)");
        db.execSQL("INSERT INTO question(question_id, question_number, quiz_id, question_text, question_type_id, question_img_url)" +
                "VALUES (2, 1, 2, '1+1', 1, NULL)");
        db.execSQL("INSERT INTO question(question_id, question_number, quiz_id, question_text, question_type_id, question_img_url)" +
                "VALUES (3, 2, 2, '1+0', 1, NULL)");
        db.execSQL("INSERT INTO question(question_id, question_number, quiz_id, question_text, question_type_id, question_img_url)" +
                "VALUES (4, 3, 2, '2+2', 1, NULL)");

        // Link Teacher and Student to Institution
        db.execSQL("INSERT INTO teacher_institution (teacher_institution_id, teacher_id, institution_id, enrollment_date) " +
                "VALUES (1, 2, 1, '2023-06-15')");
        db.execSQL("INSERT INTO student_institution (student_institution_id, student_id, institution_id, enrollment_date) " +
                "VALUES (1, 3, 1, '2023-09-01')");

        // Add one Module linked to Institution
        db.execSQL("INSERT INTO module (module_id, institution_id, module_name, module_description, module_key) " +
                "VALUES (4, 1, 'Math Basics', 'Fundamentals of Mathematics', 'math')");

        // Add one Quiz created by the Teacher
        db.execSQL("INSERT INTO quiz (quiz_id, quiz_type_id, quiz_title, quiz_duration, instructions, quiz_attempts, user_id, quiz_navigable, quiz_tab_restrictor, module_id, num_questions) " +
                "VALUES (1, 1, 'Math Quiz 101', 30, 'Answer all questions carefully.', 3, 2, 1, 0,4, 10)");

        // Add Questions for the Quiz
        db.execSQL("INSERT INTO question (question_id, question_number, quiz_id, question_text, question_type_id, question_img_url) " +
                "VALUES (1, 1, 1, 'What is 2 + 2?', 1, NULL)");
        db.execSQL("INSERT INTO mcq (option_id, question_id, optionA, optionB, optionC, optionD, correctOption) " +
                "VALUES (1, 1, '3', '4', '5', '6', 2)");

        // Add Quiz Attempt by Student
        db.execSQL("INSERT INTO quiz_attempt (attempt_id, quiz_id, user_id, start_time, end_time, score, status) " +
                "VALUES (1, 1, 3, '2023-09-15 10:00:00', '2023-09-15 10:30:00', 100, 'completed')");

        // Add Submission for Quiz Attempt
        db.execSQL("INSERT INTO quiz_submission (submission_id, assignment_id, question_id, user_id, selected_option_id, answer_text, is_correct, submission_date) " +
                "VALUES (1, 1, 1, 3, 2, NULL, 1, '2023-09-15 10:15:00')");

        // Add Feedback for Student Attempt
        db.execSQL("INSERT INTO feedback (feedback_id, user_id, assignment_id, feedback_text, is_visible) " +
                "VALUES (1, 2, 1, 'Great job! Perfect score.', 1)");

        Log.d("DatabaseHelper", "Database seeded successfully with a single institution, teacher, student, quiz, and related data!");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        // Create tables
        db.execSQL("CREATE TABLE user_role (user_role_id INTEGER PRIMARY KEY, role_name TEXT, role_description TEXT)");
        db.execSQL("CREATE TABLE user (user_id INTEGER PRIMARY KEY, user_email TEXT, user_password TEXT, user_name TEXT, user_role_id INTEGER, joined_date TEXT, FOREIGN KEY(user_role_id) REFERENCES user_role(user_role_id))");
        db.execSQL("CREATE TABLE institution (user_id INTEGER PRIMARY KEY, institution_name TEXT, institution_phone TEXT, institution_address TEXT, institution_enrolment_key TEXT UNIQUE, institution_date_joined TEXT, FOREIGN KEY(user_id) REFERENCES user(user_id))");
        db.execSQL("CREATE TABLE module (module_id INTEGER PRIMARY KEY, institution_id INTEGER, module_name TEXT UNIQUE, module_description TEXT, module_key TEXT UNIQUE, FOREIGN KEY(institution_id) REFERENCES institution(user_id))");
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
        // Handle database upgrade as needed
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
