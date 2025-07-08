package com.app.hammocklife;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.app.hammocklife.custom.BSImagePicker;
import com.app.hammocklife.custom.Utility;

public class MarketPlace extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_place);
        AppCompatImageButton img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        AppCompatImageView img_logo1 = findViewById(R.id.img_logo1);
        img_logo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mosquitohammock.com/"));
                startActivity(browserIntent);
            }
        });
        AppCompatImageView img_logo2 = findViewById(R.id.img_logo2);
        img_logo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://socohammocks.com/"));
                startActivity(browserIntent);
            }
        });
        AppCompatImageView img_logo3 = findViewById(R.id.img_logo3);
        img_logo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Blue Ridge Camping Hammock", "Suspension System"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MarketPlace.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Blue Ridge Camping Hammock")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/Lawson-Hammock-Camping-Rainfly-Included/dp/B000YLIX7W/ref=as_sl_pc_tf_til?tag=hammocklife03-20&linkCode=w00&linkId=09ab8c53130bad726fb3e8b950a75ea7&creativeASIN=B000YLIX7W"));
                            startActivity(browserIntent);
                        }else if (items[item].equals("Suspension System")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/Lawson-Hammock-Camping-Suspension-System/dp/B07C889H9Y/ref=as_sl_pc_tf_til?tag=hammocklife03-20&linkCode=w00&linkId=f39a79be65bed1ef5ae06973ac6b81ba&creativeASIN=B07C889H9Y"));
                            startActivity(browserIntent);
                        }
                    }
                });
                builder.show();
            }
        });
        AppCompatImageView img_logo4 = findViewById(R.id.img_logo4);
        img_logo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Hammock Bliss Bug Free Sky Bed", "Hammock Bliss Double", "ammock Bliss Sky Tent 2"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MarketPlace.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Hammock Bliss Bug Free Sky Bed")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/Hammock-Bliss-Sky-Bed-Free/dp/B00HZCZ4AC/ref=as_sl_pc_tf_til?tag=hammocklife03-20&linkCode=w00&linkId=184063f4cc9f0797058639d77939cc44&creativeASIN=B00HZCZ4AC"));
                            startActivity(browserIntent);
                        }else if (items[item].equals("Hammock Bliss Double")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/Hammock-Bliss-Double-Backpacking-Suspension/dp/B0000ATOFK/ref=as_sl_pc_tf_til?tag=hammocklife03-20&linkCode=w00&linkId=d972cfad8e41f320a40991dded47265a&creativeASIN=B0000ATOFK"));
                            startActivity(browserIntent);
                        }else if (items[item].equals("ammock Bliss Sky Tent 2")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/Hammock-Bliss-Sky-Tent-Revolutionary/dp/B0054MQ11Q/ref=as_sl_pc_tf_til?tag=hammocklife03-20&linkCode=w00&linkId=42be348232b8be9c4d5a837993be3fd7&creativeASIN=B0054MQ11Q"));
                            startActivity(browserIntent);
                        }
                    }
                });
                builder.show();
            }
        });
        AppCompatImageView img_logo5 = findViewById(R.id.img_logo5);
        img_logo5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/Wise-Owl-Outfitters-Hammocks-Backpacking/dp/B01JLG8QNG/ref=as_sl_pc_tf_til?tag=hammocklife03-20&linkCode=w00&linkId=39376a2559056ec1f96d59a97b90bd95&creativeASIN=B01JLG8QNG"));
                startActivity(browserIntent);
            }
        });
        AppCompatImageView img_logo6 = findViewById(R.id.img_logo6);
        img_logo6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/Hammock-Sky-Brazilian-Double-Backyard/dp/B00PB03YA2/ref=as_sl_pc_tf_til?tag=hammocklife03-20&linkCode=w00&linkId=186a902345495eb5c41cd274711448f9&creativeASIN=B00PB03YA2"));
                startActivity(browserIntent);
            }
        });
        AppCompatImageView img_logo7 = findViewById(R.id.img_logo7);
        img_logo7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/Sorbus-Hanging-Hammock-Cushions-Included/dp/B010GA2X1M/ref=as_sl_pc_tf_til?tag=hammocklife03-20&linkCode=w00&linkId=c18e8e1bd1931936d98da9eaf77f1eda&creativeASIN=B010GA2X1M"));
                startActivity(browserIntent);
            }
        });
        AppCompatImageView img_logo8 = findViewById(R.id.img_logo8);
        img_logo8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/Best-Choice-Products-Portable-2-Person/dp/B00LOZNXRC/ref=as_sl_pc_tf_til?tag=hammocklife03-20&linkCode=w00&linkId=1b193833c6094553dfadc7ac860ee185&creativeASIN=B00LOZNXRC"));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
