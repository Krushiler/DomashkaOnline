package com.example.krushiler.domashka;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.krushiler.domashka.Swipes.OnSwipeTouchListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HomeworkActivity extends AppCompatActivity {
    LinearLayout ponl, vtl, srl, chtl, ptl, sbl, linear, raspisanie, zvonkilay;
    LinearLayout[] layouts = new LinearLayout[6];
    RadioButton bpn, bvt, bsr, bcht, bpt, bsb;
    Toolbar toolbar;
    RadioButton[] buttons = new RadioButton[6];
    Spinner sp[]=new Spinner[42];
    EditText et[] = new EditText[42];
    TextView zs[] = new TextView[16];
    String homeworkstr[] = new String[42];
    int spinnerint[] = new int[42];
    String timestr[] = new String[16];
    boolean isOnTimeLayout = false;

    int DIALOG_TIME = 1;
    int myHour = 0;
    int myMinute = 0;

    OnSwipeTouchListener ons;
    LinearLayout mainLayout;

    SharedPreferences[] sPref = new SharedPreferences[et.length];
    SharedPreferences[] szsPref = new SharedPreferences[zs.length];
    SharedPreferences[] spPref = new SharedPreferences[sp.length];

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private List<String> databaseHomework;
    private List<Integer> databaseSubjects;
    private List<String> databaseTime;

    boolean isOnRings = false;
    int currDay = 0;

    FirebaseUser user = mAuth.getInstance().getCurrentUser();

    ArrayAdapter<CharSequence> adapter;

    String userStatus;

    String editorCode;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);
        Calendar calendar = Calendar.getInstance();

        Intent intent = getIntent();
        userStatus = intent.getStringExtra("userStatus");
        editorCode = intent.getStringExtra("editorCode");

        myRef = FirebaseDatabase.getInstance().getReference();

        et[0]=(EditText) findViewById(R.id.editText3);
        et[1]=(EditText) findViewById(R.id.editText5);
        et[2]=(EditText) findViewById(R.id.editText7);
        et[3]=(EditText) findViewById(R.id.editText9);
        et[4]=(EditText) findViewById(R.id.editText11);
        et[5]=(EditText) findViewById(R.id.editText13);
        et[6]=(EditText) findViewById(R.id.editText15);
        et[7]=(EditText) findViewById(R.id.editText17);
        et[8]=(EditText) findViewById(R.id.editText19);
        et[9]=(EditText) findViewById(R.id.editText21);
        et[10]=(EditText) findViewById(R.id.editText23);
        et[11]=(EditText) findViewById(R.id.editText25);
        et[12]=(EditText) findViewById(R.id.editText27);
        et[13]=(EditText) findViewById(R.id.editText29);
        et[14]=(EditText) findViewById(R.id.editText31);
        et[15]=(EditText) findViewById(R.id.editText33);
        et[16]=(EditText) findViewById(R.id.editText35);
        et[17]=(EditText) findViewById(R.id.editText37);
        et[18]=(EditText) findViewById(R.id.editText39);
        et[19]=(EditText) findViewById(R.id.editText41);
        et[20]=(EditText) findViewById(R.id.editText43);
        et[21]=(EditText) findViewById(R.id.editText45);
        et[22]=(EditText) findViewById(R.id.editText47);
        et[23]=(EditText) findViewById(R.id.editText49);
        et[24]=(EditText) findViewById(R.id.editText51);
        et[25]=(EditText) findViewById(R.id.editText53);
        et[26]=(EditText) findViewById(R.id.editText55);
        et[27]=(EditText) findViewById(R.id.editText57);
        et[28]=(EditText) findViewById(R.id.editText59);
        et[29]=(EditText) findViewById(R.id.editText61);
        et[30]=(EditText) findViewById(R.id.editText63);
        et[31]=(EditText) findViewById(R.id.editText65);
        et[32]=(EditText) findViewById(R.id.editText67);
        et[33]=(EditText) findViewById(R.id.editText69);
        et[34]=(EditText) findViewById(R.id.editText71);
        et[35]=(EditText) findViewById(R.id.editText73);
        et[36]=(EditText) findViewById(R.id.editText75);
        et[37]=(EditText) findViewById(R.id.editText77);
        et[38]=(EditText) findViewById(R.id.editText79);
        et[39]=(EditText) findViewById(R.id.editText81);
        et[40]=(EditText) findViewById(R.id.editText83);
        et[41]=(EditText) findViewById(R.id.editText85);

        sp[0]=(Spinner) findViewById(R.id.spinner1);
        sp[1]=(Spinner) findViewById(R.id.spinner2);
        sp[2]=(Spinner) findViewById(R.id.spinner3);
        sp[3]=(Spinner) findViewById(R.id.spinner4);
        sp[4]=(Spinner) findViewById(R.id.spinner5);
        sp[5]=(Spinner) findViewById(R.id.spinner6);
        sp[6]=(Spinner) findViewById(R.id.spinner7);
        sp[7]=(Spinner) findViewById(R.id.spinner8);
        sp[8]=(Spinner) findViewById(R.id.spinner9);
        sp[9]=(Spinner) findViewById(R.id.spinner10);
        sp[10]=(Spinner) findViewById(R.id.spinner11);
        sp[11]=(Spinner) findViewById(R.id.spinner12);
        sp[12]=(Spinner) findViewById(R.id.spinner13);
        sp[13]=(Spinner) findViewById(R.id.spinner14);
        sp[14]=(Spinner) findViewById(R.id.spinner15);
        sp[15]=(Spinner) findViewById(R.id.spinner16);
        sp[16]=(Spinner) findViewById(R.id.spinner17);
        sp[17]=(Spinner) findViewById(R.id.spinner18);
        sp[18]=(Spinner) findViewById(R.id.spinner19);
        sp[19]=(Spinner) findViewById(R.id.spinner20);
        sp[20]=(Spinner) findViewById(R.id.spinner21);
        sp[21]=(Spinner) findViewById(R.id.spinner22);
        sp[22]=(Spinner) findViewById(R.id.spinner23);
        sp[23]=(Spinner) findViewById(R.id.spinner24);
        sp[24]=(Spinner) findViewById(R.id.spinner25);
        sp[25]=(Spinner) findViewById(R.id.spinner26);
        sp[26]=(Spinner) findViewById(R.id.spinner27);
        sp[27]=(Spinner) findViewById(R.id.spinner28);
        sp[28]=(Spinner) findViewById(R.id.spinner29);
        sp[29]=(Spinner) findViewById(R.id.spinner30);
        sp[30]=(Spinner) findViewById(R.id.spinner31);
        sp[31]=(Spinner) findViewById(R.id.spinner32);
        sp[32]=(Spinner) findViewById(R.id.spinner33);
        sp[33]=(Spinner) findViewById(R.id.spinner34);
        sp[34]=(Spinner) findViewById(R.id.spinner35);
        sp[35]=(Spinner) findViewById(R.id.spinner36);
        sp[36]=(Spinner) findViewById(R.id.spinner37);
        sp[37]=(Spinner) findViewById(R.id.spinner38);
        sp[38]=(Spinner) findViewById(R.id.spinner39);
        sp[39]=(Spinner) findViewById(R.id.spinner40);
        sp[40]=(Spinner) findViewById(R.id.spinner41);
        sp[41]=(Spinner) findViewById(R.id.spinner42);

        zs[0]=(TextView) findViewById(R.id.time1);
        zs[1]=(TextView) findViewById(R.id.time11);
        zs[2]=(TextView) findViewById(R.id.time2);
        zs[3]=(TextView) findViewById(R.id.time22);
        zs[4]=(TextView) findViewById(R.id.time3);
        zs[5]=(TextView) findViewById(R.id.time33);
        zs[6]=(TextView) findViewById(R.id.time4);
        zs[7]=(TextView) findViewById(R.id.time44);
        zs[8]=(TextView) findViewById(R.id.time5);
        zs[9]=(TextView) findViewById(R.id.time55);
        zs[10]=(TextView) findViewById(R.id.time6);
        zs[11]=(TextView) findViewById(R.id.time66);
        zs[12]=(TextView) findViewById(R.id.time7);
        zs[13]=(TextView) findViewById(R.id.time77);
        zs[14]=(TextView) findViewById(R.id.time8);
        zs[15]=(TextView) findViewById(R.id.time88);

        ponl=(LinearLayout) findViewById(R.id.ponl);
        vtl=(LinearLayout) findViewById(R.id.vtl);
        srl=(LinearLayout) findViewById(R.id.srl);
        chtl=(LinearLayout) findViewById(R.id.chtl);
        ptl=(LinearLayout) findViewById(R.id.ptl);
        sbl=(LinearLayout) findViewById(R.id.sbl);
        bpn=(RadioButton) findViewById(R.id.pn);
        bvt=(RadioButton) findViewById(R.id.vt);
        bsr=(RadioButton) findViewById(R.id.sr);
        bcht=(RadioButton) findViewById(R.id.cht);
        bpt=(RadioButton) findViewById(R.id.pt);
        bsb=(RadioButton) findViewById(R.id.sb);
        zvonkilay = (LinearLayout) findViewById(R.id.zvonoklayout);
        linear=(LinearLayout) findViewById(R.id.linear);
        raspisanie=(LinearLayout) findViewById(R.id.raspisanie);
        mainLayout=(LinearLayout) findViewById(R.id.homeworkxml);
        buttons[0]=bpn;
        buttons[1]=bvt;
        buttons[2]=bsr;
        buttons[3]=bcht;
        buttons[4]=bpt;
        buttons[5]=bsb;
        layouts[0]=ponl;
        layouts[1]=vtl;
        layouts[2]=srl;
        layouts[3]=chtl;
        layouts[4]=ptl;
        layouts[5]=sbl;

        toolbar=(Toolbar) findViewById(R.id.toolbar);

        int n = calendar.get(Calendar.DAY_OF_WEEK);
        String[] array = getResources().getStringArray(R.array.subjects);
        adapter =new ArrayAdapter<CharSequence>(this, R.layout.spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (int i = 0; i < sp.length; i++) {
            sp[i].setAdapter(adapter);
        }
        loadText();

        for (int i = 0; i < zs.length; i++) {
            zs[i].setTextSize(25);
            zs[i].setTextColor(Color.BLACK);
            if(zs[i].getText().toString().equals("")){
                zs[i].setText("--.--");
            }
        }
        View.OnClickListener[] listeners = new View.OnClickListener[6];
        listeners[0] = lispon;
        listeners[1] = lisvt;
        listeners[2] = lissr;
        listeners[3] = lischt;
        listeners[4] = lispt;
        listeners[5] = lissb;
        if (n!=1) {
            layouts[n - 2].setVisibility(View.VISIBLE);
            bpn.setChecked(true);
            bpn.setTextColor(Color.RED);
            linear.setVisibility(View.VISIBLE);
            if(n==2){
                bpn.setText("ПН");
                bvt.setText("ВТ");
                bsr.setText("СР");
                bcht.setText("ЧТ");
                bpt.setText("ПТ");
                bsb.setText("СБ");
                for (int i = 0; i < 6; i++) {
                    buttons[i].setOnClickListener(listeners[i]);
                }
            }
            else if (n==3){
                bpn.setText("ВТ");
                bvt.setText("СР");
                bsr.setText("ЧТ");
                bcht.setText("ПТ");
                bpt.setText("СБ");
                bsb.setText("ПН");
                bsb.setTextColor(Color.BLUE);
                for (int i = 0; i < 6; i++) {
                    if(i<5) {
                        buttons[i].setOnClickListener(listeners[i + 1]);
                    }
                    else {
                        buttons[5].setOnClickListener(listeners[0]);
                    }
                }
            }
            else if(n==4){
                bpn.setText("СР");
                bvt.setText("ЧТ");
                bsr.setText("ПТ");
                bcht.setText("СБ");
                bpt.setText("ПН");
                bsb.setText("ВТ");
                bpt.setTextColor(Color.BLUE);
                bsb.setTextColor(Color.BLUE);
                for (int i = 0; i < 6; i++) {
                    if(i<4) {
                        buttons[i].setOnClickListener(listeners[i + 2]);
                    }
                    else {
                        buttons[4].setOnClickListener(listeners[0]);
                        buttons[5].setOnClickListener(listeners[1]);
                    }
                }
            }
            else if(n==5){
                bpn.setText("ЧТ");
                bvt.setText("ПТ");
                bsr.setText("СБ");
                bcht.setText("ПН");
                bpt.setText("ВТ");
                bsb.setText("СР");
                bpt.setTextColor(Color.BLUE);
                bcht.setTextColor(Color.BLUE);
                bsb.setTextColor(Color.BLUE);
                for (int i = 0; i < 6; i++) {
                    if(i<3) {
                        buttons[i].setOnClickListener(listeners[i + 3]);
                    }
                    else {
                        buttons[3].setOnClickListener(listeners[0]);
                        buttons[4].setOnClickListener(listeners[1]);
                        buttons[5].setOnClickListener(listeners[2]);
                    }
                }
            }
            else if(n==6){
                bpn.setText("ПТ");
                bvt.setText("СБ");
                bsr.setText("ПН");
                bcht.setText("ВТ");
                bpt.setText("СР");
                bsb.setText("ЧТ");
                bsr.setTextColor(Color.BLUE);
                bcht.setTextColor(Color.BLUE);
                bpt.setTextColor(Color.BLUE);
                bsb.setTextColor(Color.BLUE);
                for (int i = 0; i < 6; i++) {
                    if(i<2) {
                        buttons[i].setOnClickListener(listeners[i + 4]);
                    }
                    else {
                        buttons[2].setOnClickListener(listeners[0]);
                        buttons[3].setOnClickListener(listeners[1]);
                        buttons[4].setOnClickListener(listeners[2]);
                        buttons[5].setOnClickListener(listeners[3]);
                    }
                }
            }
            else if(n==7){
                bpn.setText("СБ");
                bvt.setText("ПН");
                bsr.setText("ВТ");
                bcht.setText("СР");
                bpt.setText("ЧТ");
                bsb.setText("ПТ");
                bvt.setTextColor(Color.BLUE);
                bsr.setTextColor(Color.BLUE);
                bcht.setTextColor(Color.BLUE);
                bpt.setTextColor(Color.BLUE);
                bsb.setTextColor(Color.BLUE);
                for (int i = 0; i < 6; i++) {
                    if(i<1) {
                        buttons[i].setOnClickListener(listeners[i + 5]);
                    }
                    else {
                        buttons[1].setOnClickListener(listeners[0]);
                        buttons[2].setOnClickListener(listeners[1]);
                        buttons[3].setOnClickListener(listeners[2]);
                        buttons[4].setOnClickListener(listeners[3]);
                        buttons[5].setOnClickListener(listeners[4]);
                    }
                }
            }
        }
        else  {
            findViewById(R.id.vihodnoy).setVisibility(View.VISIBLE);
            for (int i = 0; i < 6; i++) {
                buttons[i].setOnClickListener(listeners[i]);
            }
        }
        ons = new OnSwipeTouchListener(this){
            public void onSwipeRight(){
                Log.d("swipe", "Right");
                if (buttons[4].isChecked()){
                    buttons[5].callOnClick();
                    buttons[5].setChecked(true);
                }
                if (buttons[3].isChecked()){
                    buttons[4].callOnClick();
                    buttons[4].setChecked(true);
                }
                if (buttons[2].isChecked()){
                    buttons[3].callOnClick();
                    buttons[3].setChecked(true);
                }
                if (buttons[1].isChecked()){
                    buttons[2].callOnClick();
                    buttons[2].setChecked(true);
                }
                if (buttons[0].isChecked()){
                    buttons[1].callOnClick();
                    buttons[1].setChecked(true);
                }
            }
            public void onSwipeLeft(){
                Log.d("swipe", "Left");
                if (buttons[1].isChecked()){
                    buttons[0].callOnClick();
                    buttons[0].setChecked(true);
                }
                if (buttons[2].isChecked()){
                    buttons[1].callOnClick();
                    buttons[1].setChecked(true);
                }
                if (buttons[3].isChecked()){
                    buttons[2].callOnClick();
                    buttons[2].setChecked(true);
                }
                if (buttons[4].isChecked()){
                    buttons[3].callOnClick();
                    buttons[3].setChecked(true);
                }
                if (buttons[5].isChecked()){
                    buttons[4].callOnClick();
                    buttons[4].setChecked(true);
                }
            }
        };
        mainLayout.setOnTouchListener(ons);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userStatus.equals("editor")) {
            getMenuInflater().inflate(R.menu.menu, menu);
        }else{
            getMenuInflater().inflate(R.menu.nonadminmenu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.oproge){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("О программе");
            alertDialog.setMessage("Данная программа является альтернативой бумажному дневнику для записи домашнего задания\n\n\nРазработчик: Лазарев Даниил\n Лицей-Интеренат №1 г.Иркутск\n\n\n\nbuild 1.2.1");
            alertDialog.setPositiveButton("Закрыть", null);
            alertDialog.show();
        }
        if(id==R.id.zvonki){
            if(!isOnRings) {
                for (int i = 0; i < buttons.length; i++) {
                    if (buttons[i].isChecked()){
                        currDay = i;
                    }
                    buttons[i].setChecked(false);
                }
                zvonkilay.setVisibility(View.VISIBLE);
                ponl.setVisibility(View.GONE);
                vtl.setVisibility(View.GONE);
                srl.setVisibility(View.GONE);
                chtl.setVisibility(View.GONE);
                ptl.setVisibility(View.GONE);
                sbl.setVisibility(View.GONE);
                linear.setVisibility(View.GONE);
                findViewById(R.id.vihodnoy).setVisibility(View.GONE);
                isOnRings = true;
            }else{
                zvonkilay.setVisibility(View.INVISIBLE);
                for (int i = 0; i < buttons.length; i++){
                    if(i == currDay){
                        buttons[i].callOnClick();
                        buttons[i].setChecked(true);
                        break;
                    }
                }
                isOnRings = false;
            }
        }
        if(id==R.id.deleteDZ){
            onClickDeleteDZ();
        }
        if(id==R.id.exit){
            Intent intent = new Intent(this, AutentificationActivity.class);
            intent.putExtra("status", "NO");
            startActivity(intent);
        }
        if(id==R.id.confirm){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeworkActivity.this);

            alertDialog.setTitle("Сохранить изменения...");

            alertDialog.setMessage("Вы уверены, что хотите сохранить изменения?");

            alertDialog.setIcon(R.drawable.save);

            alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveText();
                }
            });
            alertDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        }
        if(id==R.id.returnAll){
            for (int i = 0; i < sp.length; i ++){
                sp[i].setSelection(spinnerint[i]);
            }
            for (int i = 0; i < et.length; i++){
                et[i].setText(homeworkstr[i]);
            }
            for (int i = 0; i < zs.length; i ++){
                zs[i].setText(timestr[i]);
            }
        }
        /*if(id==R.id.subjectsrasp){
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void saveText() {
        if (userStatus.equals("editor")) {
            for (int i = 0; i < et.length; i++) {
                myRef.child(user.getUid()).child("Homework" + i).setValue(et[i].getText().toString());
            }
            for (int i = 0; i < sp.length; i++) {
                myRef.child(user.getUid()).child("Subjects" + i).setValue(sp[i].getSelectedItemPosition());
            }
            for (int i = 0; i < zs.length; i++) {
                if (zs[i].getText() == "" || zs[i].getText() == " ") {
                    myRef.child(user.getUid()).child("time" + i).setValue("-|-");
                } else {
                    myRef.child(user.getUid()).child("time" + i).setValue(zs[i].getText().toString());
                }
            }
        }
    }

    private void loadText() {

        myRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s = ((CharSequence)dataSnapshot.child("EditorCode").getValue()).toString();
                if (editorCode.equals(s)){
                    userStatus = "editor";
                    for (int i = 0; i < sp.length; i ++){
                        sp[i].setEnabled(true);
                    }
                    for (int i = 0; i < et.length; i ++){
                        et[i].setEnabled(true);
                        et[i].setTextColor(Color.BLACK);
                    }
                    for (int i = 0; i < zs.length; i ++){
                        zs[i].setEnabled(true);
                    }
                }else{
                    userStatus = "guest";
                    for (int i = 0; i < sp.length; i ++){
                        sp[i].setEnabled(false);
                        sp[i].setOnTouchListener(ons);
                        sp[i].setOnTouchListener(ons);
                    }
                    for (int i = 0; i < et.length; i ++){
                        et[i].setEnabled(false);
                        et[i].setTextColor(Color.BLACK);
                        et[i].setOnTouchListener(ons);
                    }
                    for (int i = 0; i < zs.length; i ++){
                        zs[i].setEnabled(false);
                        zs[i].setTextColor(Color.BLACK);
                    }
                }
                setSupportActionBar(toolbar);
                for (int i = 0; i < et.length; i++) {
                    homeworkstr[i] = (String) dataSnapshot.child("Homework" + i).getValue();
                    et[i].setText(homeworkstr[i]);
                }
                for (int i = 0; i < sp.length; i++) {
                    if (dataSnapshot.child("Subjects" + i).getValue(Integer.class)!=null) {
                        int position = dataSnapshot.child("Subjects" + i).getValue(Integer.class);
                        spinnerint[i] = position;
                        sp[i].setSelection(position);
                    }
                }
                for (int i = 0; i < zs.length; i++) {
                    if ((CharSequence) dataSnapshot.child("time" + i).getValue() != "") {
                        timestr[i] = (String) dataSnapshot.child("time" + i).getValue();
                        zs[i].setText(timestr[i]);
                    }else{
                        zs[i].setText("-|-");
                    }
                }for (int i = 0; i < zs.length; i ++){
                    if (zs[i].getText().toString().equals("") || zs[i].getText().toString().equals(" ")){
                        zs[i].setText("-|-");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        saveText();
    }
    TextView tv;
    public void onclick(View view) {
        showDialog(DIALOG_TIME);
        tv =(TextView) findViewById(view.getId());
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TIME) {
            TimePickerDialog tpd = new TimePickerDialog(this, myCallBack, myHour, myMinute, true);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;
            tv.setText(myHour + ":" + myMinute);
            hourOfDay = 0;
            minute = 0;
        }
    };

    private View.OnClickListener lispon = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i = 0; i < layouts.length; i++){
                layouts[i].setVisibility(View.GONE);
            }
            ponl.setVisibility(View.VISIBLE);
            linear.setVisibility(View.VISIBLE);
            zvonkilay.setVisibility(View.GONE);
            findViewById(R.id.vihodnoy).setVisibility(View.GONE);
            isOnRings = false;
        }
    };
    private View.OnClickListener lisvt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i = 0; i < layouts.length; i++){
                layouts[i].setVisibility(View.GONE);
            }
            vtl.setVisibility(View.VISIBLE);
            linear.setVisibility(View.VISIBLE);
            zvonkilay.setVisibility(View.GONE);
            findViewById(R.id.vihodnoy).setVisibility(View.GONE);
            isOnRings = false;
        }
    };
    private View.OnClickListener lissr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i = 0; i < layouts.length; i++){
                layouts[i].setVisibility(View.GONE);
            }
            srl.setVisibility(View.VISIBLE);
            linear.setVisibility(View.VISIBLE);
            zvonkilay.setVisibility(View.GONE);
            findViewById(R.id.vihodnoy).setVisibility(View.GONE);
            isOnRings = false;
        }
    };
    private View.OnClickListener lischt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i = 0; i < layouts.length; i++){
                layouts[i].setVisibility(View.GONE);
            }
            chtl.setVisibility(View.VISIBLE);
            linear.setVisibility(View.VISIBLE);
            zvonkilay.setVisibility(View.GONE);
            findViewById(R.id.vihodnoy).setVisibility(View.GONE);
            isOnRings = false;
        }
    };
    private View.OnClickListener lispt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i = 0; i < layouts.length; i++){
                layouts[i].setVisibility(View.GONE);
            }
            ptl.setVisibility(View.VISIBLE);
            linear.setVisibility(View.VISIBLE);
            zvonkilay.setVisibility(View.GONE);
            findViewById(R.id.vihodnoy).setVisibility(View.GONE);
            isOnRings = false;
        }
    };
    private View.OnClickListener lissb = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i = 0; i < layouts.length; i++){
                layouts[i].setVisibility(View.GONE);
            }
            sbl.setVisibility(View.VISIBLE);
            linear.setVisibility(View.VISIBLE);
            zvonkilay.setVisibility(View.GONE);
            findViewById(R.id.vihodnoy).setVisibility(View.GONE);
            isOnRings = false;
        }
    };

    public void onClickDeleteDZ(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeworkActivity.this);

        alertDialog.setTitle("Подтвердить удаление...");

        alertDialog.setMessage("Вы уверены, что хотите  удалить ДЗ на этот день?");

        alertDialog.setIcon(R.drawable.rubbish_bin);

        alertDialog.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                if(ponl.getVisibility() == View.VISIBLE){
                    for (int i = 0; i < 7; i++) {
                        et[i].setText("");
                        saveText();
                    }
                }
                else if(vtl.getVisibility() == View.VISIBLE){
                    for (int i = 7; i < 14; i++) {
                        et[i].setText("");
                        saveText();
                    }
                }
                else if(srl.getVisibility() == View.VISIBLE){
                    for (int i = 14; i < 21; i++) {
                        et[i].setText("");
                        saveText();
                    }
                }
                else if(chtl.getVisibility() == View.VISIBLE){
                    for (int i = 21; i < 28; i++) {
                        et[i].setText("");
                        saveText();
                    }
                }
                else if(ptl.getVisibility() == View.VISIBLE){
                    for (int i = 28; i < 35; i++) {
                        et[i].setText("");
                        saveText();
                    }
                }
                else if(sbl.getVisibility() == View.VISIBLE){
                    for (int i = 35; i < 42; i++) {
                        et[i].setText("");
                        saveText();
                    }
                }
                Toast.makeText(getApplicationContext(), "ДЗ удалены", Toast.LENGTH_SHORT).show();
            }
        });


        alertDialog.setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getApplicationContext(), "Вы нажали НЕТ", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}