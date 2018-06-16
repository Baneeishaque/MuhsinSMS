package project.muhsin.sms;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Send extends Activity {

	
	ProgressDialog dialog = null;
	private String receiver;
	private String message;
	private EditText txtreceiver;
	private EditText txtmessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send);

		txtreceiver=(EditText)findViewById(R.id.editText1);
		txtmessage=(EditText)findViewById(R.id.editText2);

		// Get intent, action and MIME type
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				handleSendText(intent); // Handle text being sent
			}
		}
	}

	void handleSendText(Intent intent) {
		String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (sharedText != null) {
			// Update UI to reflect text being shared
			//Toast.makeText(getApplicationContext(),sharedText,Toast.LENGTH_LONG).show();
			txtmessage.setText(sharedText);
		}
	}



	public void SMSSend(View v)
	{

		receiver = txtreceiver.getText().toString();
		

		message = txtmessage.getText().toString();
		
		if(receiver.isEmpty())
		{
			Toast.makeText(getApplicationContext(), "Empty Receiver", Toast.LENGTH_SHORT).show();
			
			
				
			
		}
		
		else
		{
			dialog = ProgressDialog.show(Send.this, "", 
                    "Sending Message...", true);
			 new Thread(new Runnable() {
				    public void run() {
				    	sign();					      
				    }
				  }).start();

			
   	
		}
	}
	
	
	void sign(){
		try{			
			 
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://filebooker.com.md-1.webhostbox.net/ndksolutions/way2sms/send.php"); // make sure the url is correct.
			
			//add your data
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			
			// Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
			// $Edittext_value = $_POST['Edittext_value'];
			nameValuePairs.add(new BasicNameValuePair("to",receiver));  
			nameValuePairs.add(new BasicNameValuePair("msg",message));
		
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			//Execute HTTP Post Request
			// edited by James from coderzheaven.. from here....
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			final String response = httpclient.execute(httppost, responseHandler);
			
			/*runOnUiThread(new Runnable() {
			    public void run() {
			    	Toast.makeText(Send.this,"Response from PHP : " + response,Toast.LENGTH_LONG).show();
					dialog.dismiss();
			    }
			});*/
			
			if(response.contains("1")){
				runOnUiThread(new Runnable() {
				    public void run() {
				    	dialog.dismiss();
				    	Toast.makeText(Send.this,"Message Sended Successfully", Toast.LENGTH_SHORT).show();
				    }
				});
				
				
			}else{
				runOnUiThread(new Runnable() {
				    public void run() {
				    	dialog.dismiss();
				    	Toast.makeText(Send.this,"Got Some error,Contact Banee Ishaque K", Toast.LENGTH_SHORT).show();
				    }
				});				
			}
			
		}catch(final Exception e){
			runOnUiThread(new Runnable() {
			    public void run() {
			    	dialog.dismiss();
			    	Toast.makeText(Send.this,"Exception : " + e.getMessage(),Toast.LENGTH_LONG).show();
					
			    }
			});
			
		}
	}

	// Declare
	static final int PICK_CONTACT=1;
	public void contact(View v)
	{
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

		startActivityForResult(intent, PICK_CONTACT);

	}


	//code
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
			case (PICK_CONTACT) :
				if (resultCode == Activity.RESULT_OK) {

					Uri contactData = data.getData();
					Cursor c =  managedQuery(contactData, null, null, null, null);
					if (c.moveToFirst()) {


						String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

						String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

						if (hasPhone.equalsIgnoreCase("1")) {
							Cursor phones = getContentResolver().query(
									ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
									ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
									null, null);
							phones.moveToFirst();

							String cNumber = phones.getString(phones.getColumnIndex("data1"));

							if(cNumber.contains("+"))
							{
								cNumber=cNumber.substring(1);
							}
							while (cNumber.contains(" "))
							{
								int in=cNumber.indexOf(" ");
								//String n=cNumber.substring(0,in);
								cNumber=cNumber.substring(0,in)+cNumber.substring(in+1,cNumber.length());
								//Toast.makeText(getApplicationContext(),n,Toast.LENGTH_LONG).show();
								//Toast.makeText(getApplicationContext(),cNumber,Toast.LENGTH_LONG).show();
							}
							txtreceiver.setText( cNumber);
							//Toast.makeText(getApplicationContext(),"number is:"+cNumber,Toast.LENGTH_SHORT).show();
						}



					}
				}
				break;
		}
	}
}
