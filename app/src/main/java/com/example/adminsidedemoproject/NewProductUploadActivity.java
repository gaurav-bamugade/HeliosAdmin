package com.example.adminsidedemoproject;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.adminsidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewProductUploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
//activityxml views
NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    Button chooseProductImage,uploadProductDetails;
ImageButton close_dialogue_top_button,close_dialogue_unit_top_button;
ImageView productImg;
EditText productName,productDescription,productPurchasePrice,productMrpPrice,productSellPrice,
    productSelectUnitType,productLowStockQuantity,productSelectCategory,productAvailableQuantity;
private Dialog categoryDialogue,unitDialogue;
private DatabaseReference cateRef,unitRef;

//dialogue buttons
Button  addNewCategoryButton,selectCategory,selectUnit,addNewUnit;
Spinner add_product_category_spinner,add_unit_spinner;
ArrayList<String> categoryList,unitList;
ArrayAdapter<String> categoryAdapter,unitAdapter;
Uri imageUri;
String downloadUrl;
String timestamp=""+System.currentTimeMillis();

    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product_upload);
        initialize();
        setCategoryDialog();
        setUnitDialog();
        loadCategory();
        loadUnit();
        //category
        categoryList=new ArrayList<>();
        categoryAdapter=new ArrayAdapter<>(this, R.layout.style_spinner,categoryList);
        add_product_category_spinner.setAdapter(categoryAdapter);
        LoadingBar=new ProgressDialog(this);
        //unit
        unitList=new ArrayList<>();
        unitAdapter=new ArrayAdapter<>(this, R.layout.style_spinner,unitList);
        add_unit_spinner.setAdapter(unitAdapter);

        if(ContextCompat.checkSelfPermission(NewProductUploadActivity.this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(NewProductUploadActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },100);
        }
        if(ContextCompat.checkSelfPermission(NewProductUploadActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(NewProductUploadActivity.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },200);
        }


    }
    public void initialize(){
        chooseProductImage=findViewById(R.id.choose_product_image);
        uploadProductDetails=findViewById(R.id.uploadProduct);
        productImg=findViewById(R.id.prod_img);
        productName=findViewById(R.id.product_Name);
        productDescription=findViewById(R.id.product_description);
        productPurchasePrice=findViewById(R.id.purchase_price);
        productMrpPrice=findViewById(R.id.mrp_price);
        productSellPrice=findViewById(R.id.sell_price);
        productSelectUnitType=findViewById(R.id.select_unit_type);
        productLowStockQuantity=findViewById(R.id.low_stock_quantity);
        productSelectCategory=findViewById(R.id.select_category);
        productAvailableQuantity=findViewById(R.id.available_quantity);

        productSelectUnitType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unitDialogue.show();
            }
        });

        productSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialogue.show();
            }
        });

       /* chooseProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               *//* Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);*//*
            }
        });
*/
        chooseProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CharSequence options[]=new CharSequence[]
                        {
                                "Camera",
                                "Gallery",
                        };
                AlertDialog.Builder builder= new AlertDialog.Builder(NewProductUploadActivity.this);
                builder.setTitle("Select The Options For Image:");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i)
                    {
                        if(i==0)
                        {
                            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,100);
                        }

                        if(i==1)
                        {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, 200);
                        }

                    }
                });
                builder.show();
            }
        });

        uploadProductDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfImagePresent();
            }
        });

    }


    //////////////
    public void loadCategory()
    {
        cateRef=  FirebaseDatabase.getInstance().getReference();
        cateRef.child("ProductCategoriesAndUnits").child("ProductCategories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
            {

                if(snapshot.exists())
                {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {

                        String name=ds.child("categoryName").getValue().toString();
                        categoryList.add((name));
                        categoryAdapter.notifyDataSetChanged();
                    }
                }
                 else
                {
                    Toast.makeText(NewProductUploadActivity.this,"Please add category..",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    public void loadUnit()
    {
        unitRef=  FirebaseDatabase.getInstance().getReference();
        unitRef.child("ProductCategoriesAndUnits").child("ProductUnits").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {

                        String name=ds.child("unitName").getValue().toString();
                        unitList.add((name));
                        unitAdapter.notifyDataSetChanged();
                    }
                }
                else
                {
                    Toast.makeText(NewProductUploadActivity.this,"Please add category..",Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void setCategoryDialog() {
        Calendar calendar= Calendar.getInstance();

        SimpleDateFormat FromToDateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        categoryDialogue =new Dialog(this);
        categoryDialogue.setContentView(R.layout.add_product_category_dialog);
        categoryDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
        categoryDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        categoryDialogue.setCancelable(true);
        add_product_category_spinner=categoryDialogue.findViewById(R.id.add_product_category_spinner);
        addNewCategoryButton=categoryDialogue.findViewById(R.id.add_new_category_button);
        selectCategory=categoryDialogue.findViewById(R.id.select_category_btn);

        close_dialogue_top_button=categoryDialogue.findViewById(R.id.close_dialogue_top_button);
        close_dialogue_top_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialogue.dismiss();
            }
        });
        selectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productSelectCategory.setText(add_product_category_spinner.getSelectedItem().toString());
            }
        });

        addNewCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(NewProductUploadActivity.this,AddCategoryForProduct.class);
                startActivity(i);
            }
        });
    }
    private void setUnitDialog() {
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat FromToDateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        unitDialogue =new Dialog(this);
        unitDialogue.setContentView(R.layout.add_unit_for_product_dialogue);
        unitDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
        unitDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        unitDialogue.setCancelable(true);
        add_unit_spinner=unitDialogue.findViewById(R.id.add_unit_spinner);
        selectUnit=unitDialogue.findViewById(R.id.select_unit);
        addNewUnit=unitDialogue.findViewById(R.id.add_new_unit);
        close_dialogue_unit_top_button=unitDialogue.findViewById(R.id.close_dialogue_unit_top_button);
        close_dialogue_unit_top_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unitDialogue.dismiss();
            }
        });
        selectUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productSelectUnitType.setText(add_unit_spinner.getSelectedItem().toString());
            }
        });
        addNewUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(NewProductUploadActivity.this,AddNewUnit.class);
                startActivity(i);
            }
        });
    }
   @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && data!=null)
        {
           // Bitmap captureImage=(Bitmap) data.getExtras().get("data");
           // productImg.setImageBitmap(captureImage);

            Bitmap bitmapImage =(Bitmap) data.getExtras().get("data");

            int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
            // your_imageview.setImageBitmap(scaled);
            //Toast.makeText(this, ""+scaled.toString(), Toast.LENGTH_SHORT).show();
            imageUri=data.getData();
            productImg.setImageBitmap(scaled);
        }
        else if(requestCode==200 && data!=null)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {
                    MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmapImage =BitmapFactory.decodeFile(picturePath);
            int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
           // your_imageview.setImageBitmap(scaled);
            //Toast.makeText(this, ""+scaled.toString(), Toast.LENGTH_SHORT).show();
            imageUri=data.getData();
            productImg.setImageBitmap(scaled);

        }
    }

    public void checkIfImagePresent(){
        if(productImg.getDrawable() == null)
        {

      //      Toast.makeText(this, "empty "+imageUri.toString(), Toast.LENGTH_SHORT).show();
            uploadDetailsWithOutImg();
        }
        else
        {
           uploadDetailsWithImage();
//            Toast.makeText(this, "exist "+imageUri.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public void uploadDetailsWithImage(){
        //loading.show();
        LoadingBar.setTitle("Please wait..");
        LoadingBar.setMessage("Please wait,uploading product...");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();
        StorageReference ref= FirebaseStorage.getInstance().getReference().child("productImages").child(timestamp);
//        StorageReference imgRef=ref.child("productImages").child(imageUri.getLastPathSegment());
        productImg.setDrawingCacheEnabled(true);
        productImg.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) productImg.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] data = baos.toByteArray();
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

      //  UploadTask uploadTask = imgRef.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull  Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            downloadUrl=task.getResult().toString();
                            uploadWithimg();
                            finish();
                        }
                        else
                        {

                            Toast.makeText(NewProductUploadActivity.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    Toast.makeText(NewProductUploadActivity.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void uploadDetailsWithOutImg(){
        LoadingBar.setTitle("Please wait..");
        LoadingBar.setMessage("Please wait,uploading product...");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();
        String prodName=productName.getText().toString().trim();
        String prodDesc=productDescription.getText().toString().trim();
        String purchasePrice=productPurchasePrice.getText().toString().trim();
        String mrpPrice= productMrpPrice.getText().toString().trim();
        String sellPrice=productSellPrice.getText().toString().trim();
        String unit=productSelectUnitType.getText().toString().trim();
        String lowQuantity=productLowStockQuantity.getText().toString().trim();
        String category=productSelectCategory.getText().toString().trim();
        String availableStock=productAvailableQuantity.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String currentDateandTime = sdf.format(new Date());

        SimpleDateFormat twelehourformat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        String currenttwelehrformat= twelehourformat.format(new Date());

        if(TextUtils.isEmpty(prodName)
                && TextUtils.isEmpty(prodDesc)
                &&TextUtils.isEmpty(purchasePrice)
                &&TextUtils.isEmpty(mrpPrice)
                && TextUtils.isEmpty(sellPrice)
                &&TextUtils.isEmpty(unit)
                &&TextUtils.isEmpty(lowQuantity)
                &&TextUtils.isEmpty(category)
                &&TextUtils.isEmpty(availableStock))
        {
            Toast.makeText(NewProductUploadActivity.this,"Please Enter Required Credentials",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if(!prodName.isEmpty() && !prodDesc.isEmpty()
            && !purchasePrice.isEmpty() && !mrpPrice.isEmpty()
            && !sellPrice.isEmpty() && !unit.isEmpty()
            && !lowQuantity.isEmpty() && !category.isEmpty() && !availableStock.isEmpty() )
            {

                String id= UUID.randomUUID().toString();
                HashMap<String,Object> map =new HashMap<>();
                map.put("ProdId",id);
                map.put("ProductName",prodName.trim());
                map.put("ProductDescription",prodDesc.trim());
                map.put("ProductPurchasePrice",purchasePrice.trim());
                map.put("MrpPrice",mrpPrice.trim());
                map.put("SellPrice",sellPrice.trim());
                map.put("UnitType",unit.trim());
                map.put("LowQuantity",lowQuantity.trim());
                map.put("Category",category.trim());
                map.put("AvailableStock",availableStock.trim());
                map.put("UpdateTime",currenttwelehrformat.trim());
                map.put("Timestamp",availableStock.trim());
                map.put("UploadTime",currentDateandTime.trim());
                map.put("OpeningStock",availableStock.trim());
                map.put("StockIn","0.00");
                map.put("StockOut","0.00");
                map.put("ProdImage","https://firebasestorage.googleapis.com/v0/b/heliosenterprisesm-f9c59.appspot.com/o/noimage.png?alt=media&token=4155621c-0387-4644-9227-069ca3c84de9");
                FirebaseDatabase data=FirebaseDatabase.getInstance();
                data.getReference().child("AllProducts").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            LoadingBar.dismiss();
                            Toast.makeText(NewProductUploadActivity.this,"Product Uploaded Sucessfull..",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            LoadingBar.dismiss();
                            Toast.makeText(NewProductUploadActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                        }
                        //loading.dismiss();
                    }
                });
            }
        }
    }
    private void uploadWithimg()
    {

        String prodName=productName.getText().toString();
        String prodDesc=productDescription.getText().toString();
        String purchasePrice=productPurchasePrice.getText().toString();
        String mrpPrice= productMrpPrice.getText().toString();
        String sellPrice=productSellPrice.getText().toString();
        String unit=productSelectUnitType.getText().toString();
        String lowQuantity=productLowStockQuantity.getText().toString();
        String category=productSelectCategory.getText().toString();
        String availableStock=productAvailableQuantity.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String currentDateandTime = sdf.format(new Date());
        SimpleDateFormat twelehourformat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        String currenttwelehrformat= twelehourformat.format(new Date());
        if(TextUtils.isEmpty(prodName)
                && TextUtils.isEmpty(prodDesc)
                &&TextUtils.isEmpty(purchasePrice)
                &&TextUtils.isEmpty(mrpPrice)
                && TextUtils.isEmpty(sellPrice)
                &&TextUtils.isEmpty(unit)
                &&TextUtils.isEmpty(lowQuantity)
                &&TextUtils.isEmpty(category)
                &&TextUtils.isEmpty(availableStock))
        {

            Toast.makeText(NewProductUploadActivity.this,"Please Enter Required Credentials",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if(!prodName.isEmpty() && !prodDesc.isEmpty()
                    && !purchasePrice.isEmpty() && !mrpPrice.isEmpty()
                    && !sellPrice.isEmpty() && !unit.isEmpty()
                    && !lowQuantity.isEmpty() && !category.isEmpty() && !availableStock.isEmpty() )
            {
                String id= UUID.randomUUID().toString();
                HashMap<String,Object> map =new HashMap<>();
                map.put("ProdId",id);
                map.put("ProductName",prodName);
                map.put("ProductDescription",prodDesc);
                map.put("ProductPurchasePrice",purchasePrice);
                map.put("MrpPrice",mrpPrice);
                map.put("SellPrice",sellPrice);
                map.put("UnitType",unit);
                map.put("LowQuantity",lowQuantity);
                map.put("Category",category);
                map.put("AvailableStock",availableStock);
                map.put("UpdateTime",currenttwelehrformat);
                map.put("Timestamp",timestamp);
                map.put("UploadTime",currentDateandTime);
                map.put("OpeningStock",availableStock);
                map.put("StockIn","0");
                map.put("StockOut","0");
                map.put("ProdImage",downloadUrl);
                FirebaseDatabase data=FirebaseDatabase.getInstance();
                data.getReference().child("AllProducts").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseDatabase categoryWiseProducts=FirebaseDatabase.getInstance();
                            categoryWiseProducts.getReference().child("categoryWiseProducts").child(category).child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(NewProductUploadActivity.this,"Product Uploaded Sucessfull..",Toast.LENGTH_SHORT).show();
                                    LoadingBar.dismiss();
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(NewProductUploadActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                            LoadingBar.dismiss();
                        }
                        //loading.dismiss();
                    }
                });
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    protected void onStart() {
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}






//imageupload
 /*   private Uri outputUri;
    static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    static final String FILE_NAMING_PREFIX = "JPEG_";
    static final String FILE_NAMING_SUFFIX = "_";
    static final String FILE_FORMAT = ".jpg";
    static final String AUTHORITY_SUFFIX = ".cropper.fileprovider";
    private final ActivityResultLauncher<Uri> takePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), this::onTakePictureResult);
    private final ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(), this::onCropImageResult);*/








 /*  private void uploadData()
    {
        loading.show();
        StorageReference ref= FirebaseStorage.getInstance().getReference();
        StorageReference imgRef=ref.child("prodimg").child(image.getLastPathSegment());

        UploadTask uploadTask = imgRef.putFile(image);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull  Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            downloadurl=task.getResult().toString();
                            uploadCategory();
                            finish();
                        }
                        else
                        {

                            Toast.makeText(NewProductUploadActivity.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    Toast.makeText(NewProductUploadActivity.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadCategory()
    {
        String prodName=productName.getText().toString();
        String prodDesc=productDescription.getText().toString();
        String prodPrice=productPrice.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String currentDateandTime = sdf.format(new Date());

        if(TextUtils.isEmpty(prodName) && TextUtils.isEmpty(prodDesc) &&TextUtils.isEmpty(prodPrice) &&TextUtils.isEmpty(prodDesc) ){

            Toast.makeText(NewProductUploadActivity.this,"Please Enter Required Credentials",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            String id= UUID.randomUUID().toString();
            HashMap<String,Object> map =new HashMap<>();
            map.put("prod_id",id);
            map.put("product_name",prodName);
            map.put("product_description",prodDesc);
            map.put("product_price",Integer.parseInt(prodPrice));
            map.put("stock_count",totalquantity);
            map.put("time",currentDateandTime);
            map.put("product_image_url",downloadurl);
            map.put("category",cate);


            FirebaseDatabase data=FirebaseDatabase.getInstance();
            data.getReference().child("products").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful())
                    {

                        Toast.makeText(NewProductUploadActivity.this,"Please Enter Required Credentials",Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Toast.makeText(NewProductUploadActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();
                }
            });


        }
    }*/


/*
    private String cate;
    private Button btnChooseImg,btnUploadProduct;
    private ImageView prod_img;

    private TextInputEditText productName,productDescription,productPrice;*/
   /* private AutoCompleteTextView gender;
    private String currentuserid;
    private FirebaseAuth fireauth;
    private DatabaseReference firedb;
    private static final int GalleryPick=1;
    private StorageReference userprofileimageref;
    private ProgressDialog LoadingBar;
    private Uri imageuri;
    public RecyclerView cate_rc;

    FirebaseDatabase dat=FirebaseDatabase.getInstance();
    DatabaseReference mr=dat.getReference();
    private Dialog loading,categoryDialog;

    private CircleImageView addimg;
    private Button addbtn;
    private EditText categoryName;
    private Uri image;
    private Toolbar vr;
    private String downloadurl;
    private ImageView plusbtn,minusbtn;
    private TextView stockCtn;
private int totalquantity=1;*/














































/*
    public void onTakePictureResult(boolean success) {
        if (success) { startCameraWithUri(); }
        else { showErrorMessage("taking picture failed"); }
    }

    public void startCameraWithUri() {
        CropImageContractOptions options = new CropImageContractOptions(outputUri, new CropImageOptions())
                .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setAspectRatio(1, 1)
                .setMaxZoom(4)
                .setAutoZoomEnabled(true)
                .setMultiTouchEnabled(true)
                .setCenterMoveEnabled(true)
                .setShowCropOverlay(true)
                .setAllowFlipping(true)
                .setSnapRadius(3f)
                .setTouchRadius(48f)
                .setInitialCropWindowPaddingRatio(0.1f)
                .setBorderLineThickness(3f)
                .setBorderLineColor(Color.argb(170, 255, 255, 255))
                .setBorderCornerThickness(2f)
                .setBorderCornerOffset(5f)
                .setBorderCornerLength(14f)
                .setBorderCornerColor(Color.WHITE)
                .setGuidelinesThickness(1f)
                .setGuidelinesColor(R.color.white)
                .setBackgroundColor(Color.argb(119, 0, 0, 0))
                .setMinCropWindowSize(24, 24)
                .setMinCropResultSize(20, 20)
                .setMaxCropResultSize(99999, 99999)
                .setActivityTitle("")
                .setActivityMenuIconColor(0)
                .setOutputUri(null)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(90)
                .setRequestedSize(0, 0)
                .setRequestedSize(0, 0, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setInitialCropWindowRectangle(null)
                .setInitialRotation(90)
                .setAllowCounterRotation(false)
                .setFlipHorizontally(false)
                .setFlipVertically(false)
                .setCropMenuCropButtonTitle(null)
                .setCropMenuCropButtonIcon(0)
                .setAllowRotation(true)
                .setNoOutputImage(false)
                .setFixAspectRatio(false);
        cropImage.launch(options);
    }

    public void onCropImageResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {
            handleCropImageResult(Objects.requireNonNull(result.getUriContent())
                    .toString()
                    .replace("file:", ""));
        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {
            showErrorMessage("cropping image was cancelled by the user");
        } else {
            showErrorMessage("cropping image failed");
        }
    }

    public void handleCropImageResult(@org.jetbrains.annotations.NotNull String imageuri) {
        Uri uri= Uri.parse(imageuri);
        productImg.setImageURI(uri);
    }
    public void showErrorMessage(@org.jetbrains.annotations.NotNull String message) {
        Log.e("Camera Error:", message);
        Toast.makeText(getApplicationContext(), "Crop failed: " + message, Toast.LENGTH_SHORT).show();
    }
    private File createImageFile() throws IOException {
        SimpleDateFormat timeStamp = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                FILE_NAMING_PREFIX + timeStamp + FILE_NAMING_SUFFIX,
                FILE_FORMAT,
                storageDir
        );
    }

*/




  /* cate=getIntent().getStringExtra("cate");


        btnChooseImg=findViewById(R.id.btnchoose);
        btnUploadProduct=findViewById(R.id.uploadProduct);
        productName=findViewById(R.id.product_Name);
        productDescription=findViewById(R.id.product_description);
        productPrice=findViewById(R.id.product_price);
        plusbtn=findViewById(R.id.plus);
        minusbtn=findViewById(R.id.minus);
        stockCtn=findViewById(R.id.stockCt);
        prod_img=findViewById(R.id.prod_img);


        loading=new Dialog(this);
       // loading.setContentView(R.layout.loading);
     //   loading.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loading.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loading.setCancelable(false);


        plusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalquantity<10)
                {
                    totalquantity++;
                    stockCtn.setText(String.valueOf(totalquantity));
                }
            }
        });

       minusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalquantity>0)
                {
                    totalquantity--;
                    stockCtn.setText(String.valueOf(totalquantity));
                }
            }
        });

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v)
            {

                Intent i=new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,101);
             }
        });

        btnUploadProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productName.getText().toString().isEmpty() && productDescription.getText().toString().isEmpty() && productPrice.getText().toString().isEmpty())
                {
                    productName.setError("Required");
                    productDescription.setError("Required");
                    productPrice.setError("Required");


                    return;
                }
                if(image==null)
                {
                    Toast.makeText(NewProductUploadActivity.this,"PLease select image", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadData();
            }
        });
*/