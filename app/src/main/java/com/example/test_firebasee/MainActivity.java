package com.example.test_firebasee;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editTextName, editTextEmail;
    private Button buttonAdd, buttonUpdate, buttonDelete;
    private ListView listViewStudents;
    private List<Student> studentList;
    private FirebaseDatabaseHelper databaseHelper;
    private String selectedStudentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonDelete = findViewById(R.id.buttonDelete);
        listViewStudents = findViewById(R.id.listViewStudents);
        studentList = new ArrayList<>();
        databaseHelper = new FirebaseDatabaseHelper();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudent();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStudent();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStudent();
            }
        });

        listViewStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student selectedStudent = studentList.get(position);
                selectedStudentId = selectedStudent.getId();
                editTextName.setText(selectedStudent.getName());
                editTextEmail.setText(selectedStudent.getEmail());
            }
        });

        loadStudents();
    }

    private void addStudent() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String id = databaseHelper.getReference().push().getKey();
        if (id != null && !name.isEmpty() && !email.isEmpty()) {
            Student student = new Student(id, name, email);
            databaseHelper.addStudent(student);
            Toast.makeText(this, "Student added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStudent() {
        if (selectedStudentId == null) {
            Toast.makeText(this, "Please select a student first", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        if (!name.isEmpty() && !email.isEmpty()) {
            Student student = new Student(selectedStudentId, name, email);
            databaseHelper.updateStudent(selectedStudentId, student);
            Toast.makeText(this, "Student updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteStudent() {
        if (selectedStudentId == null) {
            Toast.makeText(this, "Please select a student first", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseHelper.deleteStudent(selectedStudentId);
        Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show();
    }

    private void loadStudents() {
        databaseHelper.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                studentList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Student student = postSnapshot.getValue(Student.class);
                    studentList.add(student);
                }
                // Cập nhật ListView adapter
                StudentListAdapter adapter = new StudentListAdapter(MainActivity.this, studentList);
                listViewStudents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }
}