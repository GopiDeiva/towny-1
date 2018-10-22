package products.towny;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import products.towny.Helpers.Utils;
import products.towny.Interfaces.ApiInterface;
import products.towny.Models.OTPResponse;
import products.towny.WebHandler.WebRequesthandler;
import retrofit2.Call;
import retrofit2.Callback;

import static products.towny.Helpers.Utils.IsNetworkAvailable;
import static products.towny.Helpers.Utils.MyPREFERENCES;

public class Entry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String phoneNo = sharedpreferences.getString(Utils.PhoneNumber, "");
        if (!phoneNo.equals("")) {
            if (IsNetworkAvailable(this)) {
                Intent move = new Intent(getApplicationContext(), Home.class);
                startActivity(move);
            }
        } else {
            final EditText phoneNumber = findViewById(R.id.phone_number);
            Button butt = findViewById(R.id.get_otp);
            butt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (IsNetworkAvailable(Entry.this)) {
                        VerifyPhoneNumber(phoneNumber.getText().toString());
                    }
                }
            });

            phoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        VerifyPhoneNumber(phoneNumber.getText().toString());
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void VerifyPhoneNumber(String phone)
    {
        if(!phone.isEmpty())
        {
            if(phone.length()!=10 || !Pattern.matches("[0-9]+", phone))
            {
                Toast.makeText(getApplicationContext(),"Please enter a valid phone number", Toast.LENGTH_LONG).show();
            }
            else {
                //TODO: Handle network request here
                getOTP(phone);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please enter the phone number", Toast.LENGTH_LONG).show();
        }
    }

    public void getOTP(final String phoneNo) {
        final ProgressDialog p = Utils.GetProcessDialog(this);
        ApiInterface apiService =
                WebRequesthandler.getClient().create(ApiInterface.class);

        Call<OTPResponse> call = apiService.GetOTP(phoneNo);
        call.request();
        call.enqueue(new Callback<OTPResponse>() {
            @Override
            public void onResponse(Call<OTPResponse> call, retrofit2.Response<OTPResponse> response) {
                Log.d("ProductResult", response.body().toString());
                p.dismiss();
                Intent move = new Intent(getApplicationContext(), OTPRequest.class);
                move.putExtra(getString(R.string.PhoneNumber), phoneNo);
                move.putExtra("otp", response.body().getOtp());
                startActivity(move);
                Toast.makeText(getApplicationContext(),response.body().getOtp(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<OTPResponse> call, Throwable t) {
                p.dismiss();
                Toast.makeText(getApplicationContext(),"failiure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}