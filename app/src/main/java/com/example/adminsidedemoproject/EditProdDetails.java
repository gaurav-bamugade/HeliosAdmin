package com.example.adminsidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.adminsidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class EditProdDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    Button EditchooseProductImage,EdituploadProductDetails;
    ImageView EditproductImg;
    EditText EditproductName,EditproductDescription,EditproductPurchasePrice,EditproductMrpPrice,EditproductSellPrice,
            EditproductSelectUnitType,EditproductLowStockQuantity,EditproductSelectCategory,EditproductAvailableQuantity;
    private Dialog EditcategoryDialogue,EditunitDialogue;
    private DatabaseReference EditcateRef,EditunitRef;

    //dialogue buttons
    Button  EditaddNewCategoryButton,EditselectCategory,EditselectUnit,EditaddNewUnit;
    Spinner Editadd_product_category_spinner,Editadd_unit_spinner;
    ArrayList<String> EditcategoryList,unitList;
    ArrayAdapter<String> EditcategoryAdapter,EditunitAdapter;
    Uri EditimageUri;
    String EditdownloadUrl;
    String Edittimestamp=""+System.currentTimeMillis();
    Toolbar toolbar;
    String AvailableStock,Category,LowQuantity,MrpPrice, ProdId,
            ProdImage, ProductDescription,ProductName,
            ProductPurchasePrice,SellPrice,Timestamp,
            UnitType,UpdateTime,UploadTime,StockIn,StockOut,OpeningStock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_prod_details);
        initialize();
        setCategoryDialog();
        setUnitDialog();
        loadCategory();
        loadUnit();
        toolbar=findViewById(R.id.edit_stock_items);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        ///setalldata
        Intent i=getIntent();
        AvailableStock=i.getStringExtra("AvailableStock");
        ProdImage=i.getStringExtra("ProdImage");
        ProductName=i.getStringExtra("ProductName");
        ProductDescription=i.getStringExtra("ProductDescription");
        ProductPurchasePrice=i.getStringExtra("ProductPurchasePrice");
        MrpPrice=i.getStringExtra("MrpPrice");
        SellPrice=i.getStringExtra("SellPrice");
        UnitType=i.getStringExtra("UnitType");
        LowQuantity=i.getStringExtra("LowQuantity");
        Category=i.getStringExtra("Category");
        ProdId=i.getStringExtra("ProdId");


        Picasso.get().load(ProdImage).into(EditproductImg);
        EditproductName.setText(ProductName);
        EditproductDescription.setText(ProductDescription);
        EditproductPurchasePrice.setText(ProductPurchasePrice);
        EditproductMrpPrice.setText(MrpPrice);
        EditproductSellPrice.setText(SellPrice);
        EditproductSelectUnitType.setText(UnitType);
        EditproductLowStockQuantity.setText(LowQuantity);
        EditproductSelectCategory.setText(Category);
        EditproductAvailableQuantity.setText(AvailableStock);


        //category
        EditcategoryList=new ArrayList<>();
        EditcategoryAdapter=new ArrayAdapter<>(this, R.layout.style_spinner,EditcategoryList);
        Editadd_product_category_spinner.setAdapter(EditcategoryAdapter);

        //unit
        unitList=new ArrayList<>();
        EditunitAdapter=new ArrayAdapter<>(this, R.layout.style_spinner,unitList);
        Editadd_unit_spinner.setAdapter(EditunitAdapter);

        if(ContextCompat.checkSelfPermission(EditProdDetails.this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(EditProdDetails.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },100);
        }
        if(ContextCompat.checkSelfPermission(EditProdDetails.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(EditProdDetails.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },200);
        }


    }
    public void initialize(){
        EditchooseProductImage=findViewById(R.id.Edit_choose_product_image);
        EdituploadProductDetails=findViewById(R.id.Edit_uploadProduct);
        EditproductImg=findViewById(R.id.Edit_prod_img);
        EditproductName=findViewById(R.id.Edit_product_Name);
        EditproductDescription=findViewById(R.id.Edit_product_description);
        EditproductPurchasePrice=findViewById(R.id.Edit_purchase_price);
        EditproductMrpPrice=findViewById(R.id.Edit_mrp_price);
        EditproductSellPrice=findViewById(R.id.Edit_sell_price);
        EditproductSelectUnitType=findViewById(R.id.Edit_select_unit_type);
        EditproductLowStockQuantity=findViewById(R.id.Edit_low_stock_quantity);
        EditproductSelectCategory=findViewById(R.id.Edit_select_category);
        EditproductAvailableQuantity=findViewById(R.id.Edit_available_quantity);

        EditproductSelectUnitType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditunitDialogue.show();
            }
        });

        EditproductSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditcategoryDialogue.show();
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
        EditchooseProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CharSequence options[]=new CharSequence[]
                        {
                                "Camera",
                                "Gallery",
                        };
                AlertDialog.Builder builder= new AlertDialog.Builder(EditProdDetails.this);
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

        EdituploadProductDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfImagePresent();
            }
        });

    }





    //////////////
    public void loadCategory()
    {
        EditcateRef=  FirebaseDatabase.getInstance().getReference();
        EditcateRef.child("ProductCategoriesAndUnits").child("ProductCategories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
            {

                if(snapshot.exists())
                {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {

                        String name=ds.child("categoryName").getValue().toString();
                        EditcategoryList.add((name));
                        EditcategoryAdapter.notifyDataSetChanged();
                    }
                }
                else
                {
                    Toast.makeText(EditProdDetails.this,"Please add category..",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    public void loadUnit()
    {
        EditunitRef=  FirebaseDatabase.getInstance().getReference();
        EditunitRef.child("ProductCategoriesAndUnits").child("ProductUnits").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {

                        String name=ds.child("unitName").getValue().toString();
                        unitList.add((name));
                        EditunitAdapter.notifyDataSetChanged();
                    }
                }
                else
                {
                    Toast.makeText(EditProdDetails.this,"Please add category..",Toast.LENGTH_SHORT).show();
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
        EditcategoryDialogue =new Dialog(this);
        EditcategoryDialogue.setContentView(R.layout.add_product_category_dialog);
        EditcategoryDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
        EditcategoryDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        EditcategoryDialogue.setCancelable(true);
        Editadd_product_category_spinner=EditcategoryDialogue.findViewById(R.id.add_product_category_spinner);
        EditaddNewCategoryButton=EditcategoryDialogue.findViewById(R.id.add_new_category_button);
        EditselectCategory=EditcategoryDialogue.findViewById(R.id.select_category_btn);


        EditselectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditproductSelectCategory.setText(Editadd_product_category_spinner.getSelectedItem().toString());
            }
        });

        EditaddNewCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(EditProdDetails.this,AddCategoryForProduct.class);
                startActivity(i);
            }
        });
    }
    private void setUnitDialog() {
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat FromToDateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        EditunitDialogue =new Dialog(this);
        EditunitDialogue.setContentView(R.layout.add_unit_for_product_dialogue);
        EditunitDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
        EditunitDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        EditunitDialogue.setCancelable(true);
       Editadd_unit_spinner=EditunitDialogue.findViewById(R.id.add_unit_spinner);
        EditselectUnit=EditunitDialogue.findViewById(R.id.select_unit);
        EditaddNewUnit=EditunitDialogue.findViewById(R.id.add_new_unit);
        EditselectUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditproductSelectUnitType.setText(Editadd_unit_spinner.getSelectedItem().toString());
            }
        });
        EditaddNewUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(EditProdDetails.this,AddNewUnit.class);
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
           // Toast.makeText(this, ""+scaled.toString(), Toast.LENGTH_SHORT).show();
            EditimageUri=data.getData();
            EditproductImg.setImageBitmap(scaled);
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
            Bitmap bitmapImage = BitmapFactory.decodeFile(picturePath);
            int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
            // your_imageview.setImageBitmap(scaled);
          //  Toast.makeText(this, ""+scaled.toString(), Toast.LENGTH_SHORT).show();
            EditimageUri=data.getData();
            EditproductImg.setImageBitmap(scaled);

        }
    }

    public void checkIfImagePresent(){
        if(EditproductImg.getDrawable() == null)
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

        StorageReference ref= FirebaseStorage.getInstance().getReference().child("productImages").child(Edittimestamp);
//        StorageReference imgRef=ref.child("productImages").child(imageUri.getLastPathSegment());
        EditproductImg.setDrawingCacheEnabled(true);
        EditproductImg.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) EditproductImg.getDrawable()).getBitmap();
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
                            EditdownloadUrl=task.getResult().toString();
                            uploadWithimg();
                            finish();
                        }
                        else
                        {

                            Toast.makeText(EditProdDetails.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditProdDetails.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void uploadDetailsWithOutImg(){

        String prodName=EditproductName.getText().toString();
        String prodDesc=EditproductDescription.getText().toString();
        String purchasePrice=EditproductPurchasePrice.getText().toString();
        String mrpPrice= EditproductMrpPrice.getText().toString();
        String sellPrice=EditproductSellPrice.getText().toString();
        String unit=EditproductSelectUnitType.getText().toString();
        String lowQuantity=EditproductLowStockQuantity.getText().toString();
        String category=EditproductSelectCategory.getText().toString();
        String availableStock=EditproductAvailableQuantity.getText().toString();
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

            Toast.makeText(EditProdDetails.this,"Please Enter Required Credentials",Toast.LENGTH_SHORT).show();
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
                map.put("Timestamp",availableStock);
                map.put("UploadTime",currentDateandTime);
                map.put("OpeningStock",availableStock);
                map.put("StockIn","0.00");
                map.put("StockOut","0.00");
                map.put("ProdImage","https://firebasestorage.googleapis.com/v0/b/heliosenterprisesm-f9c59.appspot.com/o/noimage.png?alt=media&token=4155621c-0387-4644-9227-069ca3c84de9");
                FirebaseDatabase data=FirebaseDatabase.getInstance();
                data.getReference().child("AllProducts").child(ProdId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                        if(task.isSuccessful())
                        {

                            Toast.makeText(EditProdDetails.this,"Product Uploaded Sucessfull..",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(EditProdDetails.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                        }
                        //loading.dismiss();
                    }
                });
            }
        }
    }


    private void uploadWithimg()
    {

        String prodName=EditproductName.getText().toString();
        String prodDesc=EditproductDescription.getText().toString();
        String purchasePrice=EditproductPurchasePrice.getText().toString();
        String mrpPrice= EditproductMrpPrice.getText().toString();
        String sellPrice=EditproductSellPrice.getText().toString();
        String unit=EditproductSelectUnitType.getText().toString();
        String lowQuantity=EditproductLowStockQuantity.getText().toString();
        String category=EditproductSelectCategory.getText().toString();
        String availableStock=EditproductAvailableQuantity.getText().toString();
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

            Toast.makeText(EditProdDetails.this,"Please Enter Required Credentials",Toast.LENGTH_SHORT).show();
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
                map.put("Timestamp",Edittimestamp);
                map.put("UploadTime",currentDateandTime);
                map.put("OpeningStock",availableStock);
                map.put("StockIn","0");
                map.put("StockOut","0");
                map.put("ProdImage",EditdownloadUrl);
                FirebaseDatabase data=FirebaseDatabase.getInstance();
                data.getReference().child("AllProducts").child(ProdId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseDatabase categoryWiseProducts=FirebaseDatabase.getInstance();
                            categoryWiseProducts.getReference().child("categoryWiseProducts").child(category).child(ProdId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(EditProdDetails.this,"Product Uploaded Sucessfull..",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(EditProdDetails.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i=new Intent(this,MainActivity.class);
                startActivity(i);
                return true;

        }
        return super.onOptionsItemSelected(item);
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