package com.example.krushiler.domashka;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.krushiler.domashka.Swipes.OnSwipeTouchListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.co.senab.photoview.PhotoViewAttacher;

public class HomeworkActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };



    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    public class CheckConnectionThread extends Thread {
        @Override
        public void run() {
            while(true) {
                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    if (!haveInternet) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                haveInternet = true;
                                mAuth = FirebaseAuth.getInstance();
                                mAuth.signInWithEmailAndPassword(mail + "@li1irk.ru", "li1irk").addOnCompleteListener(HomeworkActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        loadText();
                                        toolbar.setTitleTextColor(Color.rgb(0, 0, 0));
                                    }
                                });
                            }
                        });
                    }
                }else{
                    if (haveInternet){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                haveInternet = false;
                                userStatus = "guest";
                                setSupportActionBar(toolbar);
                                toolbar.setTitleTextColor(Color.RED);
                                for (int i = 0; i < sp.length; i++) {
                                    sp[i].setEnabled(false);
                                    //sp[i].setOnTouchListener(ons);
                                }
                                for (int i = 0; i < et.length; i++) {
                                    et[i].setEnabled(false);
                                    et[i].setTextColor(Color.BLACK);
                                    //et[i].setOnTouchListener(ons);
                                }
                                for (int i = 0; i < zs.length; i++) {
                                    zs[i].setEnabled(false);
                                    zs[i].setTextColor(Color.BLACK);
                                }
                                addPhotoBtn.setVisibility(View.GONE);
                            }
                        });

                    }
                }
            }
        }
    }

    LinearLayout ponl, vtl, srl, chtl, ptl, sbl, linear, raspisanie, zvonkilay, fileslay;
    ScrollView mainScrollView;
    LinearLayout[] layouts = new LinearLayout[8];
    RadioButton bpn, bvt, bsr, bcht, bpt, bsb;
    Toolbar toolbar;
    Button addPhotoBtn;
    RadioButton[] buttons = new RadioButton[6];
    Spinner sp[]=new Spinner[60];
    EditText et[] = new EditText[60];
    TextView zs[] = new TextView[20];
    String homeworkstr[] = new String[60];
    int spinnerint[] = new int[60];
    String timestr[] = new String[20];
    boolean isOnTimeLayout = false;
    boolean loadedImages = false;
    List<String> fileList = new ArrayList<String>();
    List<String> stringList = new ArrayList<String>();
    List<String> files = new ArrayList<String>();
    ListView lvfiles;
    View viewForPopup;
    ValueEventListener valueEventListener;

    volatile boolean isDownloadedImages = true, isDeletedImage = true;

    PhotoViewAttacher mAttacher;

    SharedPreferences themeSP;
    SharedPreferences.Editor themeSPEd;

    int DIALOG_TIME = 1;
    int myHour = 0;
    int myMinute = 0;

    int positionOfImage;

    ImageView imageViewForShow;
    LinearLayout layoutForShow;
    TextView textViewForShow;

    OnSwipeTouchListener ons;
    LinearLayout mainLayout;

    SharedPreferences etPref;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DatabaseReference userRef;
    private List<String> databaseHomework;
    private List<Integer> databaseSubjects;
    private List<String> databaseTime;
    String storageReferenceString;
    FileCache fileCache = new FileCache(this);
    File cacheDir;

    boolean isOnRings = false;
    int currDay = 0;

    boolean canDelete = false;

    //List<MyPair<String, String>> fileMap = new ArrayList<MyPair<String, String>>();
    //Map<String, String> fileMap = new HashMap<String, String>();

    FirebaseUser user = mAuth.getInstance().getCurrentUser();
    FirebaseStorage firebaseStorage;
    StorageReference storageReference, imagesRef;

    ArrayAdapter<CharSequence> adapter;

    String userStatus = "guest";

    String editorCode, mail;

    ImageListAdapter imageListAdapter;

    boolean haveInternet = true;

    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);
        Calendar calendar = Calendar.getInstance();

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }

        final Intent intent = getIntent();
        haveInternet = intent.getBooleanExtra("connected", true);
        userStatus = intent.getStringExtra("userStatus");
        editorCode = intent.getStringExtra("editorCode");
        mail = intent.getStringExtra("mail");

        //if(themeSP.getString("theme", "green")=="green"){

        //}else{

        //}

        cacheDir = getBaseContext().getCacheDir();

        myRef = FirebaseDatabase.getInstance().getReference();
        userRef = myRef.child(user.getUid());
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
        et[42]=(EditText) findViewById(R.id.editTextp1);
        et[43]=(EditText) findViewById(R.id.editTextp2);
        et[44]=(EditText) findViewById(R.id.editTextp3);
        et[45]=(EditText) findViewById(R.id.editTextv1);
        et[46]=(EditText) findViewById(R.id.editTextv2);
        et[47]=(EditText) findViewById(R.id.editTextv3);
        et[48]=(EditText) findViewById(R.id.editTextsr1);
        et[49]=(EditText) findViewById(R.id.editTextsr2);
        et[50]=(EditText) findViewById(R.id.editTextsr3);
        et[51]=(EditText) findViewById(R.id.editTextcht1);
        et[52]=(EditText) findViewById(R.id.editTextcht2);
        et[53]=(EditText) findViewById(R.id.editTextcht3);
        et[54]=(EditText) findViewById(R.id.editTextpt1);
        et[55]=(EditText) findViewById(R.id.editTextpt2);
        et[56]=(EditText) findViewById(R.id.editTextpt3);
        et[57]=(EditText) findViewById(R.id.editTextsb1);
        et[58]=(EditText) findViewById(R.id.editTextsb2);
        et[59]=(EditText) findViewById(R.id.editTextsb3);

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
        sp[42]=(Spinner) findViewById(R.id.spinnerp1);
        sp[43]=(Spinner) findViewById(R.id.spinnerp2);
        sp[44]=(Spinner) findViewById(R.id.spinnerp3);
        sp[45]=(Spinner) findViewById(R.id.spinnerv1);
        sp[46]=(Spinner) findViewById(R.id.spinnerv2);
        sp[47]=(Spinner) findViewById(R.id.spinnerv3);
        sp[48]=(Spinner) findViewById(R.id.spinnersr1);
        sp[49]=(Spinner) findViewById(R.id.spinnersr2);
        sp[50]=(Spinner) findViewById(R.id.spinnersr3);
        sp[51]=(Spinner) findViewById(R.id.spinnercht1);
        sp[52]=(Spinner) findViewById(R.id.spinnercht2);
        sp[53]=(Spinner) findViewById(R.id.spinnercht3);
        sp[54]=(Spinner) findViewById(R.id.spinnerpt1);
        sp[55]=(Spinner) findViewById(R.id.spinnerpt2);
        sp[56]=(Spinner) findViewById(R.id.spinnerpt3);
        sp[57]=(Spinner) findViewById(R.id.spinnersb1);
        sp[58]=(Spinner) findViewById(R.id.spinnersb2);
        sp[59]=(Spinner) findViewById(R.id.spinnersb3);

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
        zs[16]=(TextView) findViewById(R.id.time9);
        zs[17]=(TextView) findViewById(R.id.time99);
        zs[18]=(TextView) findViewById(R.id.time10);
        zs[19]=(TextView) findViewById(R.id.time1010);

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

        imageViewForShow = (ImageView) findViewById(R.id.imageViewForShow);
        layoutForShow = (LinearLayout) findViewById(R.id.layoutForShow);
        textViewForShow = (TextView) findViewById(R.id.textViewForShow);

        addPhotoBtn = (Button) findViewById(R.id.addPhotoButton);

        lvfiles = (ListView) findViewById(R.id.photoListView);

        mainScrollView = (ScrollView) findViewById(R.id.mainScrollView);

        zvonkilay = (LinearLayout) findViewById(R.id.zvonoklayout);
        linear=(LinearLayout) findViewById(R.id.linear);
        raspisanie=(LinearLayout) findViewById(R.id.raspisanie);
        mainLayout=(LinearLayout) findViewById(R.id.homeworkxml);
        fileslay=(LinearLayout) findViewById(R.id.fileslayout);
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
        layouts[6]=fileslay;
        layouts[7]=linear;
        toolbar=(Toolbar) findViewById(R.id.toolbar);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        imagesRef = storageReference.child("images");
        fileList = getArrayList("fileList");
        stringList = getArrayList("stringList");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        storageReferenceString = prefs.getString("storageReference", "");

        int n = calendar.get(Calendar.DAY_OF_WEEK);
        String[] array = getResources().getStringArray(R.array.subjects);
        adapter =new ArrayAdapter<CharSequence>(this, R.layout.spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (int i = 0; i < sp.length; i++) {
            sp[i].setAdapter(adapter);
        }

        etPref = getSharedPreferences("Prefs", MODE_PRIVATE);

        for (int i = 0; i < et.length; i ++){
            et[i].setText(etPref.getString("etPref"+i, ""));
        }
        for (int i = 0; i < zs.length; i ++){
            zs[i].setText(etPref.getString("zsPref"+i, ""));
        }
        for (int i = 0; i < sp.length; i ++){
            sp[i].setSelection(etPref.getInt("spPref"+i, 0));
        }
        if (fileList != null)
            lvfiles.setAdapter(new ImageListAdapterOffline(HomeworkActivity.this, fileList, stringList));
        Thread thread = new CheckConnectionThread();
        thread.start();

        if (!haveInternet){
            userStatus = "guest";
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.RED);

            for (int i = 0; i < sp.length; i++) {
                sp[i].setEnabled(false);
                //sp[i].setOnTouchListener(ons);
            }
            for (int i = 0; i < et.length; i++) {
                et[i].setEnabled(false);
                et[i].setTextColor(Color.BLACK);
                //et[i].setOnTouchListener(ons);
            }
            for (int i = 0; i < zs.length; i++) {
                zs[i].setEnabled(false);
                zs[i].setTextColor(Color.BLACK);
            }
            addPhotoBtn.setVisibility(View.GONE);
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
            /**Experemental */
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
        mAttacher = new PhotoViewAttacher(imageViewForShow);
        mainLayout.setOnTouchListener(ons);
        lvfiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int position, long l) {
//                if(userStatus.equals("editor")) {
                positionOfImage = position;
                viewForPopup = view;
                showMenu(view);

                /*}else {


                 *//*ImageView img= (ImageView)view.findViewById(R.id.imageforlist);
                    Bitmap bitmap=((BitmapDrawable)img.getDrawable()).getBitmap();
                    TextView tempTextView = (TextView) view.findViewById(R.id.tvforlist);
                    textViewForShow.setText(tempTextView.getText().toString());

                    onClickDaysButtons();
                    mainScrollView.setVisibility(View.GONE);
                    linear.setVisibility(View.GONE);
                    layoutForShow.setVisibility(View.VISIBLE);
                    imageViewForShow.setImageBitmap(bitmap);
                    mAttacher.update();*//*
                }*/
            }
        });
    }

    public void loadPhotos(){
        userRef.removeEventListener(valueEventListener);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s = ((CharSequence) dataSnapshot.child("EditorCode").getValue()).toString();

                if (editorCode.equals(s)) {
                    userStatus = "editor";
                    for (int i = 0; i < sp.length; i++) {
                        sp[i].setEnabled(true);
                    }
                    for (int i = 0; i < et.length; i++) {
                        et[i].setEnabled(true);
                        et[i].setTextColor(Color.BLACK);
                    }
                    for (int i = 0; i < zs.length; i++) {
                        zs[i].setEnabled(true);
                    }
                    addPhotoBtn.setVisibility(View.VISIBLE);
                } else {
                    userStatus = "guest";
                    for (int i = 0; i < sp.length; i++) {
                        sp[i].setEnabled(false);
                        //sp[i].setOnTouchListener(ons);
                    }
                    for (int i = 0; i < et.length; i++) {
                        et[i].setEnabled(false);
                        et[i].setTextColor(Color.BLACK);
                        //et[i].setOnTouchListener(ons);
                    }
                    for (int i = 0; i < zs.length; i++) {
                        zs[i].setEnabled(false);
                        zs[i].setTextColor(Color.BLACK);
                    }
                    addPhotoBtn.setVisibility(View.GONE);
                }
                setSupportActionBar(toolbar);
                for (int i = 0; i < et.length; i++) {
                    homeworkstr[i] = (String) dataSnapshot.child("Homework" + i).getValue();
                    et[i].setText(homeworkstr[i]);
                }
                for (int i = 0; i < sp.length; i++) {
                    if (dataSnapshot.child("Subjects" + i).getValue() != null) {
                        String positionString = (String) dataSnapshot.child("Subjects" + i).getValue();
                        int position = Integer.parseInt(positionString);
                        spinnerint[i] = position;
                        sp[i].setSelection(position);
                    }
                }
                for (int i = 0; i < zs.length; i++) {
                    if ((CharSequence) dataSnapshot.child("time" + i).getValue() != "") {
                        timestr[i] = (String) dataSnapshot.child("time" + i).getValue();
                        zs[i].setText(timestr[i]);
                    } else {
                        zs[i].setText("-|-");
                    }
                }
                for (int i = 0; i < zs.length; i++) {
                    if (zs[i].getText().toString().equals("") || zs[i].getText().toString().equals(" ")) {
                        zs[i].setText("-|-");
                    }
                }
                if (dataSnapshot.child("stringList").getValue() != null) {
                    stringList = new ArrayList<String>();
                    stringList = (List<String>) dataSnapshot.child("stringList").getValue();
                    saveArrayList((List<String>) dataSnapshot.child("stringList").getValue(), "stringList");
                }else{
                    stringList = new ArrayList<String>();
                }
                if( (List<String>) dataSnapshot.child("fileList").getValue()!=null){
                    files = (List<String>) dataSnapshot.child("fileList").getValue();
                }
                if( (List<String>) dataSnapshot.child("fileList").getValue()!=null){
                    fileList = (List<String>) dataSnapshot.child("fileList").getValue();
                }
                trimCache(HomeworkActivity.this);
                if ((List<String>) dataSnapshot.child("fileList").getValue() != null) {
                    canDelete = true;
                    isDownloadedImages = true;
                    saveArrayList(stringList, "stringList");
                    saveArrayList(fileList, "fileList");
                    storageReferenceString = storageReference.toString();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor prefsEd = prefs.edit();
                    prefsEd.putString("storageReference", storageReferenceString);
                    Log.d("storTag", Integer.toString(fileList.size()));
                    Log.d("storTag", files.get(0));

                    for (int i = 0; i < files.size(); i ++){
                        StorageReference imageRef = storageReference.child(files.get(i));
                        Log.d("strg", imageRef.toString());
                        File file = getCacheDir();
                        try {
                            file = File.createTempFile("image", ".jpg", HomeworkActivity.this.getCacheDir());

                            final File finalFile = file;
                            final int finalI = i;

                            imageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        /*if (finalI == 0){
                                            trimCache(HomeworkActivity.this);
                                        }*/
                                    fileList.set(finalI, Uri.parse(finalFile.getAbsolutePath()).getLastPathSegment().toString());
                                    saveArrayList(fileList, "fileList");
                                    Log.d("strg", Uri.parse(finalFile.getAbsolutePath()).getLastPathSegment().toString());

                                    lvfiles.setAdapter(new ImageListAdapterOffline(HomeworkActivity.this, fileList, stringList));
                                    lvfiles.setItemsCanFocus(true);
                                    loadedImages = true;
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    fileList = new ArrayList<String>();
                    stringList = new ArrayList<String>();
                    files = new ArrayList<String>();
                    saveArrayList(stringList, "stringList");
                    saveArrayList(fileList, "fileList");
                    lvfiles.setAdapter(null);
                    loadedImages = true;
                    isDownloadedImages = true;
                }
                if (haveInternet) {
                    SharedPreferences.Editor eted;

                    for (int i = 0; i < et.length; i++) {
                        eted = etPref.edit();
                        eted.putString("etPref" + i, et[i].getText().toString());
                        eted.commit();
                    }
                    for (int i = 0; i < sp.length; i++) {
                        eted = etPref.edit();
                        eted.putInt("spPref" + i, sp[i].getSelectedItemPosition());
                        eted.commit();
                    }
                    for (int i = 0; i < zs.length; i++) {
                        eted = etPref.edit();
                        eted.putString("zsPref" + i, zs[i].getText().toString());
                        eted.commit();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userRef.addValueEventListener(valueEventListener);
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        if (userStatus.equals("editor")) {
            popup.inflate(R.menu.popupmenuimage);
        }else{
            popup.inflate(R.menu.popupmenuimagenonadmin);
        }
        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.openPhoto:
                openPhoto();
                return true;
            case R.id.deletePhoto:
                deletePhoto();
                return true;
            case R.id.changePhotoDes:
                changePhoto();
                return true;
            case R.id.savePhotoToGallery:
                savePhoto();
                return true;
            default:
                return false;
        }
    }
    public void savePhoto(){
        ImageView img= (ImageView)viewForPopup.findViewById(R.id.imageforlist);
        Bitmap bitmap=((BitmapDrawable)img.getDrawable()).getBitmap();
        TextView tempTextView = (TextView) viewForPopup.findViewById(R.id.tvforlist);
        Random r = new Random();
        long n = r.nextLong();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, imageFileName, tempTextView.getText().toString());
        /*String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera";
        File myDir = new File(root);
        myDir.mkdirs();
        Random r = new Random();
        long n = r.nextLong();
        String fname = "Image" + Long.toString(n) + ".jpg";
        Log.d("savePhoto", root);
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
    public void openPhoto(){
        ImageView img= (ImageView)viewForPopup.findViewById(R.id.imageforlist);
        Bitmap bitmap=((BitmapDrawable)img.getDrawable()).getBitmap();
        TextView tempTextView = (TextView) viewForPopup.findViewById(R.id.tvforlist);
        textViewForShow.setText(tempTextView.getText().toString());

        onClickDaysButtons();
        mainScrollView.setVisibility(View.GONE);
        linear.setVisibility(View.GONE);
        layoutForShow.setVisibility(View.VISIBLE);
        imageViewForShow.setImageBitmap(bitmap);
    }

    public void changePhoto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Описание");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stringList.set(positionOfImage, input.getText().toString());
                userRef.child("stringList").setValue(stringList);
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    public void deletePhoto(){
        if (canDelete) {
            if (isDeletedImage) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeworkActivity.this);

                alertDialog.setTitle("Подтвердить удаление...");

                alertDialog.setMessage("Вы уверены, что хотите  удалить изображение?");

                alertDialog.setIcon(R.drawable.rubbish_bin);

                final int finalfinalposition = positionOfImage;

                alertDialog.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isDeletedImage = false;
                        lvfiles.setItemsCanFocus(false);
                        StorageReference deleteRef = storageReference.child(files.get(finalfinalposition));
                        final int finalposition = finalfinalposition;
                        deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                fileList.remove(finalposition);
                                if (files.size() != 0) {
                                    files.remove(finalposition);
                                }
                                stringList.remove(finalposition);
                                userRef.removeEventListener(valueEventListener);
                                myRef.child(user.getUid()).child("fileList").setValue(files);
                                myRef.child(user.getUid()).child("stringList").setValue(stringList);
                                saveArrayList(stringList, "stringList");
                                saveArrayList(files, "fileList");
                                userRef.addValueEventListener(valueEventListener);
                                lvfiles.setAdapter(new ImageListAdapter(HomeworkActivity.this, fileList, storageReference, stringList, getApplicationContext()));
                                lvfiles.setItemsCanFocus(true);
                                isDeletedImage = true;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                lvfiles.setItemsCanFocus(true);
                                Toast.makeText(HomeworkActivity.this, "Файл не был удалён", Toast.LENGTH_SHORT);
                                isDeletedImage = true;
                            }
                        });
                    }
                });
                alertDialog.setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getApplicationContext(), "Вы нажали НЕТ", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            } else {
                Toast.makeText(getApplicationContext(), "Удаление предыдущего файла", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(HomeworkActivity.this, "Сначала загрузите фото с сервера", Toast.LENGTH_SHORT).show();
        }
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
            alertDialog.setMessage("Данная программа является альтернативой бумажному дневнику для записи домашнего задания\n\n\nРазработчик: Лазарев Даниил\n Лицей-Интеренат №1 г.Иркутск\n\n\n\nbuild 1.7.4");
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
                onClickDaysButtons();
                fileslay.setVisibility(View.GONE);
                mainScrollView.setVisibility(View.GONE);
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
                zvonkilay.setVisibility(View.GONE);
                buttons[currDay].callOnClick();
                buttons[currDay].setChecked(true);
                mainScrollView.setVisibility(View.VISIBLE);
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
        if(id==R.id.files){
            for (int i = 0; i < layouts.length; i++){
                if (!loadedImages){
                    loadPhotos();
                }
                onClickDaysButtons();
                layouts[i].setVisibility(View.GONE);
                mainScrollView.setVisibility(View.GONE);
                fileslay.setVisibility(View.VISIBLE);
                linear.setVisibility(View.GONE);
            }
        }
        if (id==R.id.changeEditorCodeToolbar){
            onClickChangeEditorCode();
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
                myRef.child(user.getUid()).child("Subjects" + i).setValue(Integer.toString(sp[i].getSelectedItemPosition()));
            }
            for (int i = 0; i < zs.length; i++) {
                if (zs[i].getText() == "" || zs[i].getText() == " ") {
                    myRef.child(user.getUid()).child("time" + i).setValue("-|-");
                } else {
                    myRef.child(user.getUid()).child("time" + i).setValue(zs[i].getText().toString());
                }
            }
        }
        if (fileList!=null) {
            for (int i = 0; i < fileList.size(); i++) {
                Log.d("fileList", fileList.get(i));
            }
        }
    }

    private void loadText() {
        if (haveInternet) {
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String s = ((CharSequence) dataSnapshot.child("EditorCode").getValue()).toString();

                    if (editorCode.equals(s)) {
                        userStatus = "editor";
                        for (int i = 0; i < sp.length; i++) {
                            sp[i].setEnabled(true);
                        }
                        for (int i = 0; i < et.length; i++) {
                            et[i].setEnabled(true);
                            et[i].setTextColor(Color.BLACK);
                        }
                        for (int i = 0; i < zs.length; i++) {
                            zs[i].setEnabled(true);
                        }
                        addPhotoBtn.setVisibility(View.VISIBLE);
                    } else {
                        userStatus = "guest";
                        for (int i = 0; i < sp.length; i++) {
                            sp[i].setEnabled(false);
                            //sp[i].setOnTouchListener(ons);
                        }
                        for (int i = 0; i < et.length; i++) {
                            et[i].setEnabled(false);
                            et[i].setTextColor(Color.BLACK);
                            //et[i].setOnTouchListener(ons);
                        }
                        for (int i = 0; i < zs.length; i++) {
                            zs[i].setEnabled(false);
                            zs[i].setTextColor(Color.BLACK);
                        }
                        addPhotoBtn.setVisibility(View.GONE);
                    }
                    setSupportActionBar(toolbar);
                    for (int i = 0; i < et.length; i++) {
                        homeworkstr[i] = (String) dataSnapshot.child("Homework" + i).getValue();
                        et[i].setText(homeworkstr[i]);
                    }
                    for (int i = 0; i < sp.length; i++) {
                        if (dataSnapshot.child("Subjects" + i).getValue() != null) {
                            String positionString = (String) dataSnapshot.child("Subjects" + i).getValue();
                            int position = Integer.parseInt(positionString);
                            spinnerint[i] = position;
                            sp[i].setSelection(position);
                        }
                    }
                    for (int i = 0; i < zs.length; i++) {
                        if ((CharSequence) dataSnapshot.child("time" + i).getValue() != "") {
                            timestr[i] = (String) dataSnapshot.child("time" + i).getValue();
                            zs[i].setText(timestr[i]);
                        } else {
                            zs[i].setText("-|-");
                        }
                    }
                    for (int i = 0; i < zs.length; i++) {
                        if (zs[i].getText().toString().equals("") || zs[i].getText().toString().equals(" ")) {
                            zs[i].setText("-|-");
                        }
                    }

                    if (haveInternet) {
                        SharedPreferences.Editor eted;

                        for (int i = 0; i < et.length; i++) {
                            eted = etPref.edit();
                            eted.putString("etPref" + i, et[i].getText().toString());
                            eted.commit();
                        }
                        for (int i = 0; i < sp.length; i++) {
                            eted = etPref.edit();
                            eted.putInt("spPref" + i, sp[i].getSelectedItemPosition());
                            eted.commit();
                        }
                        for (int i = 0; i < zs.length; i++) {
                            eted = etPref.edit();
                            eted.putString("zsPref" + i, zs[i].getText().toString());
                            eted.commit();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            userRef.addValueEventListener(valueEventListener);
        }
    }
    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }
    @Override
    protected void onStop() {
        super.onStop();
        //saveText();
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
            String strHour, strMinute;
            strHour = Integer.toString(myHour);
            strMinute = Integer.toString(myMinute);
            if (myHour<10){
                strHour="0"+strHour;
            }
            if (myMinute<10){
                strMinute="0"+strMinute;
            }
            tv.setText(strHour + ":" + strMinute);
            hourOfDay = 0;
            minute = 0;
        }
    };

    protected void onClickDaysButtons(){
        for(int i = 0; i < layouts.length; i++){
            layouts[i].setVisibility(View.GONE);
        }
        linear.setVisibility(View.VISIBLE);
        zvonkilay.setVisibility(View.GONE);
        findViewById(R.id.vihodnoy).setVisibility(View.GONE);
        isOnRings = false;
        mainScrollView.setVisibility(View.VISIBLE);
        fileslay.setVisibility(View.GONE);
        layoutForShow.setVisibility(View.GONE);
    }



    private View.OnClickListener lispon = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickDaysButtons();
            ponl.setVisibility(View.VISIBLE);
        }
    };
    private View.OnClickListener lisvt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickDaysButtons();
            vtl.setVisibility(View.VISIBLE);
        }
    };
    private View.OnClickListener lissr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickDaysButtons();
            srl.setVisibility(View.VISIBLE);
        }
    };
    private View.OnClickListener lischt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickDaysButtons();
            chtl.setVisibility(View.VISIBLE);
        }
    };
    private View.OnClickListener lispt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickDaysButtons();
            ptl.setVisibility(View.VISIBLE);
        }
    };
    private View.OnClickListener lissb = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickDaysButtons();
            sbl.setVisibility(View.VISIBLE);
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
                    }
                    for (int i = 0; i < 3; i++) {
                        et[i+42].setText("");
                    }
                }
                else if(vtl.getVisibility() == View.VISIBLE){
                    for (int i = 7; i < 14; i++) {
                        et[i].setText("");
                    }
                    for (int i = 0; i < 3; i++) {
                        et[i+45].setText("");
                    }
                }
                else if(srl.getVisibility() == View.VISIBLE){
                    for (int i = 14; i < 21; i++) {
                        et[i].setText("");
                    }
                    for (int i = 0; i < 3; i++) {
                        et[i+48].setText("");
                    }
                }
                else if(chtl.getVisibility() == View.VISIBLE){
                    for (int i = 21; i < 28; i++) {
                        et[i].setText("");
                    }
                    for (int i = 0; i < 3; i++) {
                        et[i+51].setText("");
                    }
                }
                else if(ptl.getVisibility() == View.VISIBLE){
                    for (int i = 28; i < 35; i++) {
                        et[i].setText("");
                    }
                    for (int i = 0; i < 3; i++) {
                        et[i + 54].setText("");
                    }
                }
                else if(sbl.getVisibility() == View.VISIBLE){
                    for (int i = 35; i < 42; i++) {
                        et[i].setText("");
                    }
                    for (int i = 0; i < 3; i++) {
                        et[i+57].setText("");
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

    public void onClickAddPhoto(View v){
        if(isDownloadedImages) {
            if (loadedImages) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }else{
                Toast.makeText(this, "Сначала скачайте все фото", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(HomeworkActivity.this, "Отправка изображения. Жди...", Toast.LENGTH_SHORT).show();
        }
    }

    public interface MyCallback {
        void onCallback(List<MyPair<String, String>> value);
    }

    public void readData(final MyCallback myCallback) {
        myRef.child(String.format("fileMap")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MyPair<String, String>> value = (List<MyPair<String, String>>) dataSnapshot.getValue();
                isDownloadedImages = true;
                myCallback.onCallback(value);
                Log.d("callbacker", "first");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public boolean listContains(String uri){
        String search = uri;
        if (files!=null) {
            for (String str : files) {
                if (str.trim().contains(search))
                    return true;
            }
        }
        return false;
    }

    public String retries(String uri1){
        String uri = uri1;
        if (listContains(uri)){
            uri+="p";
            if(listContains(uri)){
                return retries(uri);
            }
        }
        return uri;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data1) {
        final Intent data = data1;
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final String[] sol = new String[1];
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Описание");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isDownloadedImages = false;
                    sol[0] = input.getText().toString();
                    Uri uri1 = null;
                    if (data != null) {
                        uri1 = data.getData();
                        String uri = uri1.getLastPathSegment().toString();
                        uri = retries(uri);
                        Log.i("PickedImage", "Uri: " + uri);
                        imagesRef = storageReference.child(uri);
                        UploadTask uploadTask = imagesRef.putFile(uri1);
                        final Uri finalUri = uri1;
                        final String finaluriString = uri;
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                userRef.removeEventListener(valueEventListener);
                                if (fileList==null){
                                    fileList = new ArrayList<String>();
                                }
                                if (stringList==null){
                                    stringList = new ArrayList<String>();
                                }
                                if (files==null){
                                    files = new ArrayList<String>();
                                }
                                fileList.add(finaluriString);
                                files.add(finaluriString);
                                stringList.add(sol[0]);
                                myRef.child(user.getUid()).child("stringList").setValue(stringList);
                                myRef.child(user.getUid()).child("fileList").setValue(files);
                                userRef.addValueEventListener(valueEventListener);
                                isDownloadedImages = true;
                                Toast.makeText(HomeworkActivity.this, "Изображение добавлено", Toast.LENGTH_SHORT).show();
                                View v = null;
                                loadPhotos();
                            }
                        });
                    }
                }
            });
            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }
    }

    public void onClickChangeEditorCode(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Код класса");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        input.setText(editorCode);

        builder.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myRef.child(user.getUid()).child("EditorCode").setValue(input.getText().toString());
                editorCode = input.getText().toString();
                SharedPreferences sharedPreferencesEditor;
                sharedPreferencesEditor = getSharedPreferences("editorPref", MODE_PRIVATE);
                SharedPreferences.Editor editorEditor = sharedPreferencesEditor.edit();
                editorEditor.putString("editor", input.getText().toString());
                editorEditor.commit();
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void showLayout(RelativeLayout rl) {
        for (int i = 0; i < layouts.length; i++) {
            onClickDaysButtons();
            layouts[i].setVisibility(View.GONE);
            mainScrollView.setVisibility(View.GONE);
            fileslay.setVisibility(View.GONE);
            linear.setVisibility(View.GONE);
        }
        rl.setVisibility(View.VISIBLE);
    }
    public void showLayout(LinearLayout  rl) {
        for (int i = 0; i < layouts.length; i++) {
            onClickDaysButtons();
            layouts[i].setVisibility(View.GONE);
            mainScrollView.setVisibility(View.GONE);
            fileslay.setVisibility(View.GONE);
            linear.setVisibility(View.GONE);
        }
        rl.setVisibility(View.VISIBLE);
    }
    public void saveArrayList(List<String> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public List<String> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}