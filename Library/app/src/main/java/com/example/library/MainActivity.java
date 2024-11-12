package com.example.library;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText idBook, name, author;
    Spinner editorial;
    Switch savailable;
    ImageButton bSave, bSearch, bEdit, bDelete, bList;
    TextView message;
    String[] arrayEditorial = {"Oveja Negra", "Prentice Hall"};
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        message = findViewById(R.id.tvMessage);
        idBook = findViewById(R.id.etIdBook);
        name = findViewById(R.id.etName);
        author = findViewById(R.id.etAuthor);
        editorial = findViewById(R.id.spEditorial);
        savailable = findViewById(R.id.swAvailable);
        bSave = findViewById(R.id.ibSave);
        bSearch = findViewById(R.id.ibSearch);
        bEdit = findViewById(R.id.ibEdit);
        bDelete = findViewById(R.id.ibDelete);
        bList = findViewById(R.id.ibList);

        ArrayAdapter<String> arrayadp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, arrayEditorial);
        editorial.setAdapter(arrayadp);

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mIdBook = idBook.getText().toString();
                String mName = name.getText().toString();
                String mAuthor = author.getText().toString();
                String mEditorial = editorial.getSelectedItem().toString();
                int mAvailable = savailable.isChecked() ? 1 : 0;

                if (checkDataBook(mIdBook, mName, mAuthor)) {
                    db.collection("book")
                            .whereEqualTo("idbook", mIdBook)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        // El ID ya existe en otro documento
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String existingName = document.getString("name");
                                            message.setTextColor(Color.parseColor("#FF4545"));
                                            message.setText("El ID del libro ya existe, asignado al libro: " + existingName);
                                        }
                                    } else {
                                        // No existe, entonces se guarda
                                        Map<String, Object> mapBook = new HashMap<>();
                                        mapBook.put("idbook", mIdBook);
                                        mapBook.put("name", mName);
                                        mapBook.put("author", mAuthor);
                                        mapBook.put("editorial", mEditorial);
                                        mapBook.put("available", mAvailable);

                                        db.collection("book")
                                                .add(mapBook)
                                                .addOnSuccessListener(documentReference -> {
                                                    message.setTextColor(Color.parseColor("#31511E"));
                                                    message.setText("Libro agregado exitosamente...");
                                                })
                                                .addOnFailureListener(e -> {
                                                    message.setTextColor(Color.parseColor("#FF4545"));
                                                    message.setText("No se agregó el libro...");
                                                });
                                    }
                                } else {
                                    message.setTextColor(Color.parseColor("#FF4545"));
                                    message.setText("Error al verificar el ID del libro.");
                                }
                            });
                } else {
                    message.setTextColor(Color.parseColor("#FF4545"));
                    message.setText("Debe ingresar todos los datos del libro");
                }
            }
        });
    }

    private boolean checkDataBook(String mIdBook, String mName, String mAuthor) {
        return !mIdBook.isEmpty() && !mName.isEmpty() && !mAuthor.isEmpty();
    }
}
