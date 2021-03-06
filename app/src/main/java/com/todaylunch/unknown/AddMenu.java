package com.todaylunch.unknown;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddMenu extends AppCompatActivity {

    private Button btn_cancel, btn_add;
    private ImageView img_delete;
    private Spinner spr1, spr2;
    private SQLiteOpenHelperIcon dbHelper = null;
    private SQLiteOpenHelperMain dbHelperMain = null;
    private ArrayList<ListObject3> arrayList;
    private ArrayList<ListObject> arrayListMain;
    private ArrayList<String> arrayList2, arrayList3;
    protected int number;
    private EditText editTextTitle, editTextLink;
    private Intent intent;
    private Date date = new Date();
    private TextView tvTitle, tvLargeGroup, tvSub, tvName, tvUrl;
    private TypefaceUtil typefaceUtil;
    private int fontNumber, btnNumber;
    private ButtonDrawableUtil btnUtil;
    private CustomProgressDialog customProgressDialog;
    private ConstraintLayout constraintLayout;

    private String getExtraTitle, getExtraLink;
    private int getExtraMainMenu, getExtraDetailMenu;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter<String> arrayAdapter2;

    //Intent -> value intent -> value choice -> confirm -> insert value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);

        this.getWindow().setStatusBarColor(MainActivity.COLOR_NUMBER);

        init();

        intent = getIntent();

        number = intent.getIntExtra("AddMenu", 0);

        //init_value();

        getExtraTitle = intent.getStringExtra("TITLE");
        getExtraMainMenu = intent.getIntExtra("MENU1", 0);
        getExtraDetailMenu = intent.getIntExtra("MENU2", 0);
        getExtraLink = intent.getStringExtra("LINK");

        fontNumber = MainActivity.FONT_NUMBER;
        btnNumber = MainActivity.BACKGROUND_NUMBER;

        NewThread nt = new NewThread(this);
        nt.execute();

        //load_value();
        //load_menu_value();

        tvTitle.setTypeface(typefaceUtil.getTypeface(fontNumber));
        tvLargeGroup.setTypeface(typefaceUtil.getTypeface(fontNumber));
        tvSub.setTypeface(typefaceUtil.getTypeface(fontNumber));
        tvName.setTypeface(typefaceUtil.getTypeface(fontNumber));
        tvUrl.setTypeface(typefaceUtil.getTypeface(fontNumber));

        editTextLink.setBackgroundColor(MainActivity.COLOR_NUMBER);
        editTextTitle.setBackgroundColor(MainActivity.COLOR_NUMBER);

        //btn_delete.setBackground(btnUtil.getDrawable(btnNumber));
        btn_cancel.setBackground(btnUtil.getDrawable(btnNumber));
        btn_add.setBackground(btnUtil.getDrawable(btnNumber));

        constraintLayout.setBackgroundColor(MainActivity.FRAMELAYOUT_NUMBER);


        arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, arrayList2);
        editTextSetter();



        //spinnerAutoSetter();

        spr1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int spinnerPosition = spr1.getSelectedItemPosition();
                spinnerMenuSetter2(spinnerPosition + 1);
                arrayAdapter2.notifyDataSetChanged();

                Log.d("Large Group Spinner :", " "+position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spr2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d("Subdivision Spinner :", " "+position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int menu1Number = (spr1.getSelectedItemPosition()) + 1;
                int menu2Number = (menu1Number * 9) + (spr2.getSelectedItemPosition() + 1);
                String menuTitle1 = editTextTitle.getText().toString();
                String strLink = editTextLink.getText().toString();

                if (getExtraTitle.equals("Nothing")) {

                    insert_value(menu1Number, menu2Number, menuTitle1, strLink);
                    Log.d("Add Menu : ", "added, " + menu1Number + ", " + menu2Number + ", " + menuTitle1 + ", " + strLink);

                } else {

                    update_value(menu1Number, menu2Number, menuTitle1, strLink);
                    Log.d("Add Menu : ", "update, " + menu1Number + ", " + menu2Number + ", " + menuTitle1 + ", " + strLink);

                }


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Add menu cancel", "Cancel");
                finish();
                Toast.makeText(AddMenu.this, R.string.toast_cancel, Toast.LENGTH_SHORT).show();
            }
        });

        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //얼럿 다이얼로그 "정말로 해당 메뉴를 삭제하시겠습니까?" 띄운 후 확인 누르면 아래 명령 진행.
                final MessageCustomDialog msgDialog = new MessageCustomDialog(AddMenu.this, getResources().getString(R.string.dialog_delete));
                msgDialog.btn_ok_msg_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SQLiteDatabase db = dbHelperMain.getWritableDatabase();
                        String deleteDb = MySQLite.SQL_DELETE + " WHERE " + MySQLite.ICOL_NAME4 + " ='" + getExtraTitle + "'";
                        db.execSQL(deleteDb);
                        db.close();

                        msgDialog.msgDialogVar.dismiss();

                        finish();

                        Log.d("Add menu : ", "delete/" + getExtraTitle);
                        Toast.makeText(AddMenu.this, R.string.toast_delete, Toast.LENGTH_SHORT).show();
                    }
                });

                msgDialog.btn_cancel_msg_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.msgDialogVar.dismiss();
                    }
                });

            }
        });


    }//onCreate*************************

    private class NewThread extends AsyncTask<Integer, Integer, Boolean> {

        private Context context;
        private CustomProgressDialog dialog;

        private NewThread(Context context) {

            this.context = context;

        }

        @Override
        protected void onPreExecute() {

            init_value();
            dialog = new CustomProgressDialog(context);
            dialog.settingCustomProgressDialog();

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            spinnerMenuSetter();

            arrayAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, arrayList2);
            spr1.setAdapter(arrayAdapter);

            arrayAdapter2 = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, arrayList3);
            spr2.setAdapter(arrayAdapter2);

            spinnerAutoSetter(arrayAdapter2);

            dialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected Boolean doInBackground(Integer... integers) {

            load_value();
            load_menu_value();


            return null;
        }
    }

    //init_value
    private void init_value() {
        dbHelper = new SQLiteOpenHelperIcon(this);
        dbHelperMain = new SQLiteOpenHelperMain(this);
    }

    private void load_value() {

        SQLiteDatabase db = dbHelperMain.getReadableDatabase();
        Cursor cursor = db.rawQuery(MySQLite.SQL_SELECT, null);
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {

            int intMenu1 = cursor.getInt(1);
            int intMenu2 = cursor.getInt(2);
            String strTitle = cursor.getString(3);
            String strLink = cursor.getString(4);

            ListObject listObject = new ListObject(intMenu1, intMenu2, strTitle, strLink);
            arrayListMain.add(listObject);

            cursor.moveToNext();
        }
        cursor.close();
        db.close();

    }

    private void load_menu_value() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(MySQLite.SQL_SELECT2, null);
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {

            int number = cursor.getInt(0);
            String title = cursor.getString(1);
            int imgNumber = cursor.getInt(2);

            ListObject3 listObject = new ListObject3(number, title, imgNumber);
            arrayList.add(listObject);

            cursor.moveToNext();
        }
        cursor.close();
        db.close();
    }

    private void insert_value(int menuNumber1, int menuNumber2, String menuTitle, String strLink) {
        SQLiteDatabase db = dbHelperMain.getWritableDatabase();

        if (overLabCheck(menuTitle)) {

            long longDate = date.getTime();

            String insertDb = MySQLite.SQL_INSERT + "(" + longDate + ", " + menuNumber1 + ", " + menuNumber2 + ", '" + menuTitle + "', '" + strLink + "')";

            db.execSQL(insertDb);
            db.close();

            Toast.makeText(AddMenu.this, R.string.toast_add, Toast.LENGTH_SHORT).show();

            finish();



        } else {

            Toast.makeText(this, R.string.toast_overlab, Toast.LENGTH_SHORT).show();

        }
    }

    private void update_value(int menuNumber1, int menuNumber2, String menuTitle, String strLink) {

        SQLiteDatabase db = dbHelperMain.getWritableDatabase();

        if(getExtraTitle.equals(menuTitle)) {

            String updateDb = MySQLite.SQL_UPDATE + MySQLite.ICOL_NAME2 + " = " + menuNumber1 + ", '" + MySQLite.ICOL_NAME3 + "' = " + menuNumber2 + ", "
                    + MySQLite.ICOL_NAME4 + " = '" + menuTitle + "', " + MySQLite.ICOL_NAME5 + " = '" + strLink + "'" + " WHERE " + MySQLite.ICOL_NAME4 + " = '" + getExtraTitle + "'";
            db.execSQL(updateDb);
            db.close();

            Toast.makeText(AddMenu.this, R.string.toast_adjust, Toast.LENGTH_SHORT).show();

            Log.d("update : ", menuNumber1 + ", " + menuNumber2 + ", " + menuTitle + ", " + strLink);

            finish();

        } else if(overLabCheck(menuTitle)) {

            String updateDb = MySQLite.SQL_UPDATE + MySQLite.ICOL_NAME2 + " = " + menuNumber1 + ", '" + MySQLite.ICOL_NAME3 + "' = " + menuNumber2 + ", "
                    + MySQLite.ICOL_NAME4 + " = '" + menuTitle + "', " + MySQLite.ICOL_NAME5 + " = '" + strLink + "'" + " WHERE " + MySQLite.ICOL_NAME4 + " = '" + getExtraTitle + "'";
            db.execSQL(updateDb);
            db.close();

            Toast.makeText(AddMenu.this, R.string.toast_adjust, Toast.LENGTH_SHORT).show();

            Log.d("update : ", menuNumber1 + ", " + menuNumber2 + ", " + menuTitle + ", " + strLink);

            finish();

        } else {

            Toast.makeText(this, R.string.toast_overlab, Toast.LENGTH_SHORT).show();

        }



    }


    private boolean overLabCheck(String menuTitle) {
        for (int i = 0; i < arrayListMain.size(); i++) {
            if (arrayListMain.get(i).getStrTitle().equals(menuTitle)) {

                return false;

            } else {
                continue;
            }
        }
        return true;
    }

    private void spinnerMenuSetter() {

        //큰메뉴 9개 나열

        for (int i = 0; i < 9; i++) {

            String menuTitle = arrayList.get(i).getmTitle();
            arrayList2.add(menuTitle);

        }


    }

    private void spinnerMenuSetter2(int spinnerValue) {
        //큰메뉴 선택된거 없으면 상세메뉴 81개 전체 나열
        //큰메뉴 선택된거 있으면 해당하는 9개 나열

        arrayList3.removeAll(arrayList3);

        if(spinnerValue == -1) {
            Log.d("Add Menu : ", "value is -1 while spinnerMenuSetter2 running");
        } else {
            if (arrayList3.size() != 0) {
                arrayList3.remove(arrayList3);
            }
            for(int i = 0; i < 9; i++) {
                arrayList3.add(arrayList.get((spinnerValue * 9) + i).getmTitle());

            }
        }

    }

    private void spinnerAutoSetter(ArrayAdapter<String> arrayAdapter) {

        if (getExtraMainMenu != 0) {

            String mainMenu = arrayList.get(getExtraMainMenu - 1).getmTitle();
            String subMenu = arrayList.get(getExtraDetailMenu - 1).getmTitle();

            int indexArray = (arrayList2.indexOf(mainMenu));

            spr1.setSelection(indexArray);
            spinnerMenuSetter2(indexArray + 1);
            arrayAdapter.notifyDataSetChanged();

            int indexArray2 = (arrayList3.indexOf(subMenu));
            spr2.setSelection(indexArray2);

            System.out.println(mainMenu + ", " + subMenu + ", " + indexArray + ", " + indexArray2);

        }

    }

    private void editTextSetter() {
        if (getExtraTitle.equals("Nothing")) {

        } else {

            editTextTitle.setText(getExtraTitle);
            editTextLink.setText(getExtraLink);

        }
    }

    private void init() {

        arrayList = new ArrayList<>();
        arrayListMain = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        arrayList3 = new ArrayList<>();
        typefaceUtil = new TypefaceUtil(this);
        btnUtil = new ButtonDrawableUtil(this);

        editTextTitle = (EditText) findViewById(R.id.et_add_title);
        editTextLink = (EditText) findViewById(R.id.et_add_link);

        tvTitle = (TextView) findViewById(R.id.tv_add_title);
        tvLargeGroup = (TextView) findViewById(R.id.tv_add_large_group);
        tvSub = (TextView) findViewById(R.id.tv_add_subdivision);
        tvName = (TextView) findViewById(R.id.tv_add_store_name);
        tvUrl = (TextView) findViewById(R.id.tv_add_url);

        btn_cancel = (Button) findViewById(R.id.btn_add_cancel);
        btn_add = (Button) findViewById(R.id.btn_add_add);
        img_delete = (ImageView) findViewById(R.id.btn_menu_delete);

        spr1 = (Spinner) findViewById(R.id.spr_add1);
        spr2 = (Spinner) findViewById(R.id.spr_add2);

        constraintLayout = findViewById(R.id.frame_add_menu);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateResources(newBase));
    }
}
