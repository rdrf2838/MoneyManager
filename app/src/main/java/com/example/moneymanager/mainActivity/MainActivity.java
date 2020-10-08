package com.example.moneymanager.mainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanager.R;
import com.example.moneymanager.imageOCR.OCRTool;
import com.example.moneymanager.utility.DatabaseHandler;
import com.example.moneymanager.utility.Payment;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String SELECTED_URI = "com.example.moneymanager.SELECTED_URI";
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int IMAGE_PICK_CODE = 102;
    private static final int IMAGE_CROP_AMOUNT_CODE = 103;
    private static final int IMAGE_CROP_DESCRIPTION_CODE = 104;
    private Uri _selectedImage;
    public DatabaseHandler _db;
    private EditText _editTextAmount;
    private EditText _editTextDescription;
    private TextView _editTextTotalAmount;
    private MainActivityAdapter myAdapter;
    private RecyclerView recyclerView;
    private List<Payment> _payments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        _db = new DatabaseHandler(this);
        _payments = _db.getAllPayments();
        _editTextAmount = findViewById(R.id.editTextTextPersonName);
        _editTextDescription = findViewById(R.id.editTextTextPersonName2);
        _editTextTotalAmount = findViewById(R.id.textView7);
        recyclerView = findViewById(R.id.recyclerView);
        myAdapter = new MainActivityAdapter(_payments, this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateAmount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateAmount();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.getFilter().filter(newText);
                updateAmount();
                return false;
            }
        });

        return true;
    }

    public void pickImageFromGallery(View view) {
        //after picking image, display in next activity window
        checkPermission();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void performCropAmount(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, IMAGE_CROP_AMOUNT_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void performCropDescription(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, IMAGE_CROP_DESCRIPTION_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    void setAmount(String result) {
        _editTextAmount.setText(result.trim().replaceAll("\\s+", ""));
    }

    void setDescription(String result) {
        _editTextDescription.setText(result);
    }

    public void addPayment(View view) {
        String description = _editTextDescription.getText().toString();
        String pricetemp = _editTextAmount.getText().toString();
        if(description.equals("") || pricetemp.equals("")){

        }else {
            double price = Double.parseDouble(pricetemp);
            Payment payment = new Payment(price, description);
            _db.addPayment(payment);
            _payments = _db.getAllPayments();
            myAdapter.setPayments(_payments);
            myAdapter.notifyDataSetChanged();
            clearPage();
        }
    }

    public void removePayments() {
        List<Payment> paymentsSelected = myAdapter.getPaymentsSelected();
        for(Payment payment : paymentsSelected) _db.deletePayment(payment);
        _payments = _db.getAllPayments();
        myAdapter.setPayments(_payments);
        myAdapter.notifyDataSetChanged();
        myAdapter.clearSelectedItems();
        clearPage();
    }

    public void updateAmount() {
        List<Payment> payments = myAdapter.getPayments();
        double total = 0;
        for(Payment payment : payments) {
            total += payment.get_cost();
        }
        _editTextTotalAmount.setText(String.valueOf(total));
    }

    public void updateAmount(View view) {
        updateAmount();
    }

    public void clearPage() {
        updateAmount();
        hideKeyboard(this);
        _editTextDescription.clearFocus();
        _editTextAmount.clearFocus();
        _editTextAmount.setText("");
        _editTextDescription.setText("");
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && null != data) {
            _selectedImage = data.getData();
            performCropAmount(_selectedImage);
        }

        else if (requestCode == IMAGE_CROP_AMOUNT_CODE) {
            if (data != null) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap selectedBitmap = extras.getParcelable("data");
                final OCRTool ocrTool = new OCRTool(this, selectedBitmap);

                class setAmountRunnable implements Runnable {
                    @Override
                    public void run() {
                        while(ocrTool.content == null) {
                            try{
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        setAmount(ocrTool.content);
                    }
                }

                new setAmountRunnable().run();
                performCropDescription(_selectedImage);
            }
        }
        else if (requestCode == IMAGE_CROP_DESCRIPTION_CODE) {
            if (data != null) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap selectedBitmap = extras.getParcelable("data");
                final OCRTool ocrTool = new OCRTool(this, selectedBitmap);

                class setDescriptionRunnable implements Runnable {
                    @Override
                    public void run() {
                        while(ocrTool.content == null) {
                            try{
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        setDescription(ocrTool.content);
                    }
                }

                new setDescriptionRunnable().run();
            }
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission },
                    requestCode);
        }
        else {
        }
    }

    public void checkPermission() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(MainActivity.this,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removeentry:
                removePayments();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}