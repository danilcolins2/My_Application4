package com.example.myapplication;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.ViewUtils;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class fragmentRegistration extends Fragment {



    EditText et_login;
    EditText et_name;
    EditText et_pass1;
    EditText et_pass2;

    SQLiteDatabase db;
    DatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);

        databaseHelper = new DatabaseHelper(getContext());

        Button button = (Button) rootView.findViewById(R.id.btn_continue);

        et_login = (EditText) rootView.findViewById(R.id.reg_login);
        et_name = (EditText) rootView.findViewById(R.id.reg_name);
        et_pass1 = (EditText) rootView.findViewById(R.id.reg_pass1);
        et_pass2 = (EditText) rootView.findViewById(R.id.reg_pass2);


        button.setOnClickListener(v -> insertUser());
        return rootView;


    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getActivity(), "Resume registration", Toast.LENGTH_SHORT).show();
        db = databaseHelper.getReadableDatabase();
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }


    private void insertUser(){
        if(checkInputs() && checkUser()){
            db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME + " (" +
                    DatabaseHelper.COLUMN_NAME + ", " +
                    DatabaseHelper.COLUMN_LOGIN + ", " +
                    DatabaseHelper.COLUMN_PASSWORD + ") VALUES (" +
                    "'" + et_name.getText().toString() + "', " +
                    "'" + et_login.getText().toString() + "', " +
                    "'" + et_pass1.getText().toString() + "');"
                    );
            Toast.makeText(getActivity(), "Вы успешно зарегестрированы, теперь вам доступна авторизация", Toast.LENGTH_SHORT).show();
            ((MainActivity)getActivity()).toLogin();
        }
    }

    private boolean checkUser(){


        String request = "SELECT * FROM " + DatabaseHelper.TABLE_NAME +
                " WHERE " + DatabaseHelper.COLUMN_LOGIN + " = " + "'" + et_login.getText().toString()+ "'";

        Cursor userCursor = db.rawQuery(request, null);
        boolean res;
        if(userCursor.moveToFirst()){
            Toast.makeText(getActivity(), "Пользователь с таким логином уже существует", Toast.LENGTH_SHORT).show();
            res =  false;
        } else {
            res =  true;
        }
        userCursor.close();
        return res;
    }

    private boolean checkInputs(){
        if(
                et_login.getText().toString().isEmpty() ||
                et_name.getText().toString().isEmpty() ||
                et_pass1.getText().toString().isEmpty() ||
                et_pass2.getText().toString().isEmpty()
        ){
            Toast.makeText(getActivity(), "Заполните пустые поля", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!et_pass1.getText().toString().equals(et_pass2.getText().toString())){
            Toast.makeText(getActivity(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}