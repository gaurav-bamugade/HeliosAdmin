package com.example.adminsidedemoproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminsidedemoproject.Adapter.ProductItemAdapter;
import com.example.adminsidedemoproject.Model.ProductsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ProductFragment extends Fragment {

private EditText search;
private RecyclerView prod_cr;
List<ProductsModel> productsModels,searchModel;
ProductItemAdapter proditemAdapter;
Button addStockItem,add_gategory_button;
    private DatabaseReference myref;
    TextView in_selling_price,in_purchase_price;
    int sellingPrice,purchasaePrice,resultsell,resultprice;
    List<Integer> sellingprice,purchasePrice;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v= inflater.inflate(R.layout.fragment_product, container, false);
        getActivity().setTitle("Stock");
        search=v.findViewById(R.id.search_prod);
        prod_cr=v.findViewById(R.id.prod_recycler);
        addStockItem=v.findViewById(R.id.add_stock_item);
        add_gategory_button=v.findViewById(R.id.add_gategory_button);
        sellingprice=new ArrayList<>();
        purchasePrice=new ArrayList<>();
        myref=  FirebaseDatabase.getInstance().getReference().child("AllProducts");
        if(myref!=null) {
            getData();
            loadpurchasesales();
        }else
        {
           // Toast.makeText(getContext(), "not exist", Toast.LENGTH_SHORT).show();
        }
        productsModels=new ArrayList<>();
        searchModel=new ArrayList<>();
        RecyclerView.LayoutManager lm=new LinearLayoutManager(getContext());
        prod_cr.setLayoutManager(lm);
        proditemAdapter=new ProductItemAdapter(getContext(),productsModels);
        prod_cr.setAdapter(proditemAdapter);
        in_selling_price=v.findViewById(R.id.in_selling_price);
        in_purchase_price=v.findViewById(R.id.in_purchase_price);
        add_gategory_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(),AddCategoryForProduct.class);
                startActivity(i);
            }
        });
        addStockItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(),NewProductUploadActivity.class);
                startActivity(i);
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                   /* proditemAdapter=new ProductItemAdapter(AllProductActivity.this,productsModels);
                    allProdRc.setAdapter(proditemAdapter);
                    proditemAdapter.notifyDataSetChanged();*/

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchModel.clear();
                if(s.toString().isEmpty())
                {
                    proditemAdapter=new ProductItemAdapter(getContext(),productsModels);
                    prod_cr.setAdapter(proditemAdapter);
                    proditemAdapter.notifyDataSetChanged();
                }
                else
                {
                    Filter(s.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s)
            {
               /* searchModel.clear();
                if(s.toString().isEmpty())
                {
                    proditemAdapter=new ProductItemAdapter(AllProductActivity.this,productsModels);
                    allProdRc.setAdapter(proditemAdapter);
                    proditemAdapter.notifyDataSetChanged();
                }
                else
                {
                    Filter(s.toString());
                }*/
            }

        });
        return v;
    }
    public void loadpurchasesales()
    {
        if(myref!=null)
        {
            myref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        String availableStock=ds.child("AvailableStock").getValue().toString();
                        String sellPrice=ds.child("SellPrice").getValue().toString();
                        String productPurchasePrice=ds.child("ProductPurchasePrice").getValue().toString();
                        int available=Integer.parseInt(availableStock);
                        int sellprice=Integer.parseInt(sellPrice);
                        int producpurchasepr=Integer.parseInt(productPurchasePrice);

                       // int availables=available+available;
                        sellingPrice=available*sellprice;
                        purchasaePrice=available*producpurchasepr;
                        /*result=sellingPrice;
                        result=result+result;*/

                        sellingprice.add(sellingPrice);

                          purchasePrice.add(purchasaePrice);
/*
                        if(sellingPrice>0)
                        {
                            Log.d("resultcheck",String.valueOf(sellingPrice));
                            in_selling_price.setText(String.valueOf(sellingPrice));
                            in_purchase_price.setText(String.valueOf(purchasaePrice));
                        }
                        else
                        {
                            in_selling_price.setText(String.valueOf(sellingPrice));
                            in_purchase_price.setText(String.valueOf(purchasaePrice));
                        }*/

                    }
                    for( int i:sellingprice)
                    {
                        resultsell=resultsell+i;
                    }
                    //Log.d("sellprice",String.valueOf(result ));
                    in_selling_price.setText(String.valueOf(resultsell));

                  for( int p:purchasePrice)
                    {
                        resultprice=resultprice+p;
                    }
                    Log.d("sellprice",String.valueOf(resultprice));
                   in_purchase_price.setText(String.valueOf(resultprice));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getData()
    {
        //myref.child("AllProducts");
        if(myref!=null)
        {
            myref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        productsModels.clear();
                        for(DataSnapshot ds:snapshot.getChildren())
                        {
                            String AvailableStock=ds.child("AvailableStock").getValue().toString();;
                            String Category=ds.child("Category").getValue().toString();
                            String LowQuantity =ds.child("LowQuantity").getValue().toString();
                            String MrpPrice =ds.child("MrpPrice").getValue().toString();
                            String ProdId =ds.child("ProdId").getValue().toString();
                            String ProdImage=ds.child("ProdImage").getValue().toString();
                            String ProductDescription=ds.child("ProductDescription").getValue().toString();;
                            String ProductName=ds.child("ProductName").getValue().toString();
                            String ProductPurchasePrice =ds.child("ProductPurchasePrice").getValue().toString();
                            String SellPrice=ds.child("SellPrice").getValue().toString();
                            String Timestamp=ds.child("Timestamp").getValue().toString();
                            String UnitType=ds.child("UnitType").getValue().toString();
                            String UpdateTime=ds.child("UpdateTime").getValue().toString();
                            String stockIn=ds.child("StockIn").getValue().toString();
                            String stockOut=ds.child("StockOut").getValue().toString();
                            String UploadTime=ds.child("UploadTime").getValue().toString();
                            String OpeningStock=ds.child("OpeningStock").getValue().toString();
                            productsModels.add(new ProductsModel(AvailableStock,Category
                                    ,LowQuantity,MrpPrice,ProdId,ProdImage,ProductDescription,ProductName
                                    ,ProductPurchasePrice,SellPrice,Timestamp,UnitType,UpdateTime,UploadTime,stockIn,stockOut,OpeningStock));
                        }
                        proditemAdapter.notifyDataSetChanged();
                        proditemAdapter.UpdateAdapter(getContext(),productsModels);
                    }
                    else{
                        //Toast.makeText(getContext(), "not exist", Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else
        {
           // Toast.makeText(getContext(), "not exist", Toast.LENGTH_SHORT).show();

        }


    }
    private void Filter(String text)
    {
        for(ProductsModel ps: productsModels)
        {
            if(ps.getProductName().equals(text))
            {
                searchModel.add(ps);
            }
        }
        proditemAdapter=new ProductItemAdapter(getContext(),searchModel);
        prod_cr.setAdapter(proditemAdapter);
        proditemAdapter.notifyDataSetChanged();
    }

   /* private void Filter(String text)
    {
        for(productsModel ps: list)
        {
            if(ps.getProductName().equals(text))
            {
                searchModel.add(ps);
            }
        }
        proditemAda=new productItemAdapter(getContext(),searchModel);
        prod_cr.setAdapter(proditemAda);
        proditemAda.notifyDataSetChanged();
    }*/
}






























  /*    getData();
        RecyclerView.LayoutManager lm=new LinearLayoutManager(getContext());
        prod_cr.setLayoutManager(lm);
        proditemAda=new productItemAdapter(getContext(),list);
        prod_cr.setAdapter(proditemAda);
        searchModel=new ArrayList<>();*/
/*        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                searchModel.clear();
                if(s.toString().isEmpty())
                {
                    proditemAda=new productItemAdapter(getContext(),list);
                    prod_cr.setAdapter(proditemAda);
                    proditemAda.notifyDataSetChanged();
                }
                else
                {
                    Filter(s.toString());
                }

            }


        });*/

