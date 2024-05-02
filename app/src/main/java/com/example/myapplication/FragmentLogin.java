package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class FragmentLogin extends Fragment {


    EditText et_login;
    EditText et_pass;

    SQLiteDatabase db;
    DatabaseHelper databaseHelper;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        et_login = (EditText) rootView.findViewById(R.id.log_login);
        et_pass = (EditText) rootView.findViewById(R.id.log_pass);

        databaseHelper = new DatabaseHelper(getContext());

        Button btn_signUp = (Button) rootView.findViewById(R.id.btn_sign_up);



        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        }

        );
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        db = databaseHelper.getReadableDatabase();
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }

    private void signUp(){
        String name = getNameByLoginAndPass();
        if (name != null){
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra("user_name", name);
            getActivity().startActivity(intent);
        }
    }
    private String getNameByLoginAndPass(){
        String name = null;

        if(checkInputs()){
            String request = "SELECT * FROM " + DatabaseHelper.TABLE_NAME +
                    " WHERE " + DatabaseHelper.COLUMN_LOGIN + "=" +  "'" + et_login.getText().toString() + "'"  + " AND " +
                    DatabaseHelper.COLUMN_PASSWORD + "=" +  "'" + et_pass.getText().toString() + "'";

            Cursor userCursor = db.rawQuery(request, null);

            if(userCursor.moveToFirst()){
                name = userCursor.getString(1);
                Toast.makeText(getActivity(), "Добро пожаловать, " + name + "!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getActivity(), "Неправильный логин или пароль", Toast.LENGTH_SHORT).show();
            }
            userCursor.close();
        }
        return name;
    }

    private boolean checkInputs(){
        if(et_login.getText().toString().isEmpty() || et_pass.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Заполните пустые поля", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}