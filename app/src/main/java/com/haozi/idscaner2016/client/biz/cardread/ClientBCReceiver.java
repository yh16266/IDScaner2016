package com.haozi.idscaner2016.client.biz.cardread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.haozi.idscaner2016.client.bean.client.BCardInfo;
import com.haozi.idscaner2016.client.ui.home.HomeActivity;
import com.routon.idr.idrinterface.readcard.ReadMode;
import com.routon.idr.idrinterface.readcard.ReadState;
import com.routon.idr.idrinterface.readcard.ReadType;
import com.routon.idrconst.Action;
import com.routon.idrconst.ActionKey;
import com.routon.idrconst.iDRConst;

public class ClientBCReceiver extends BroadcastReceiver {

	private final String TAG="ClientBCReceiver";
	private HomeActivity mReadCardActivity = null;
	
	public ClientBCReceiver(HomeActivity activity) {
		super();
		// TODO Auto-generated constructor stub
		mReadCardActivity = activity;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		String action=intent.getAction();		
		//Log.d(TAG,"onReceive = " + action);
		Message send_msg = new Message();
		if(action.equals(Action.ACTION_READER_STATE_CHANGED))
		{
			if((mReadCardActivity!=null) &&(mReadCardActivity.getMainHandler()!=null)){
				ReadState rdState = intent.getParcelableExtra(ActionKey.SERV_READCARD_NEW_READ_STATE);
				send_msg.what=MainMsg.EVT_STATE_CHANGE;    				
				send_msg.obj=rdState;
				mReadCardActivity.getMainHandler().sendMessage(send_msg);
			}
			
		}else if(action.equals(Action.ACTION_TYPEA_STATUS_CHANGED)){
			int status = intent.getIntExtra(ActionKey.SERV_READCARD_STATUS, -1);
			switch(status)
			{
				case iDRConst.MSG_TYPEA_FOUND:
					send_msg.what=MainMsg.EVT_FOUND_A;    					
					break;
					
				case iDRConst.MSG_TYPEA_OK:		    					
					send_msg.what=MainMsg.EVT_READ_CARD_A_SUCCESS;
					byte data[]=new byte[4];
					data=intent.getByteArrayExtra(ActionKey.SERV_READCARD_A_CARDNO);
					send_msg.obj=data;
					break;
				
				case iDRConst.MSG_TYPEA_REMOVED:	    					
					send_msg.what=MainMsg.EVT_LEAVE_A;
					break;
					
				case iDRConst.MSG_TYPEA_FAIL:	    					
					send_msg.what=MainMsg.EVT_READ_CARD_A_FAIL;
					break;
					
				default:	    					
					send_msg.what=MainMsg.EVT_READ_CARD_A_FAIL;
					break;		    					
			}
			if(  (mReadCardActivity!=null)
			   &&(mReadCardActivity.getMainHandler()!=null)){
				mReadCardActivity.getMainHandler().sendMessage(send_msg);
			}
			
		}else if(action.equals(Action.ACTION_TYPEB_STATUS_CHANGED)){
			int status = intent.getIntExtra(ActionKey.SERV_READCARD_STATUS, -1);
			switch(status)
			{
				case iDRConst.MSG_TYPEB_FOUND:
					send_msg.what=MainMsg.EVT_FOUND_B;
					break;
					
				case iDRConst.MSG_TYPEB_SELECTED:
					send_msg.what=MainMsg.EVT_SELECT_B;
					break;
					
				case iDRConst.MSG_TYPEB_OK: 
					BCardInfo cardInfo=new BCardInfo();
					cardInfo.name=intent.getStringExtra(ActionKey.SERV_READCARD_B_NAME);
					cardInfo.gender=intent.getStringExtra(ActionKey.SERV_READCARD_B_GENDER);
					cardInfo.nation=intent.getStringExtra(ActionKey.SERV_READCARD_B_NATION);
					cardInfo.birthday=intent.getStringExtra(ActionKey.SERV_READCARD_B_BIRTHDAY);
					cardInfo.address=intent.getStringExtra(ActionKey.SERV_READCARD_B_ADDRESS);
					cardInfo.id=intent.getStringExtra(ActionKey.SERV_READCARD_B_ID);
					cardInfo.agency=intent.getStringExtra(ActionKey.SERV_READCARD_B_AGENCY);
					cardInfo.expireStart=intent.getStringExtra(ActionKey.SERV_READCARD_B_EXPIRESTART);
					cardInfo.expireEnd=intent.getStringExtra(ActionKey.SERV_READCARD_B_EXPIREEND);
					cardInfo.photo=intent.getParcelableExtra(ActionKey.SERV_READCARD_B_PHOTO);
					cardInfo.hasFpInfo=intent.getBooleanExtra(ActionKey.SERV_READCARD_B_FLAG_FINGER,false);
					if(cardInfo.hasFpInfo)
					{
						cardInfo.fingerPrint=intent.getByteArrayExtra(ActionKey.SERV_READCARD_B_FINGERCHAR);
					}
					cardInfo.baseData = intent.getByteArrayExtra(ActionKey.SERV_READCARD_B_BASEDATA);
					cardInfo.wltData = intent.getByteArrayExtra(ActionKey.SERV_READCARD_B_WLTDATA);
					send_msg.what=MainMsg.EVT_READ_CARD_B_SUCCESS;
					send_msg.obj=cardInfo;
					break;
					
				case iDRConst.MSG_TYPEB_REMOVED:	   
					send_msg.what=MainMsg.EVT_LEAVE_B;
					break;
					
				case iDRConst.MSG_TYPEB_FAIL:
					send_msg.what=MainMsg.EVT_READ_CARD_B_FAIL;
					break;
					
				default:
					send_msg.what=MainMsg.EVT_READ_CARD_B_FAIL;
					break;	
					
			}
			if(  (mReadCardActivity!=null)
			   &&(mReadCardActivity.getMainHandler()!=null)){
				mReadCardActivity.getMainHandler().sendMessage(send_msg);
			}
			
		}else if(action.equals(Action.ACTION_READER_PAUSED)){
			if(  (mReadCardActivity!=null)
			   &&(mReadCardActivity.getMainHandler()!=null)){
				send_msg.what=MainMsg.EVT_PAUSED;  				
				mReadCardActivity.getMainHandler().sendMessage(send_msg);
			}
			
		}else if(action.equals(Action.ACTION_READER_STOPPED)){
			if(  (mReadCardActivity!=null)
			   &&(mReadCardActivity.getMainHandler()!=null)){
				send_msg.what=MainMsg.EVT_STOPPED;  				
				mReadCardActivity.getMainHandler().sendMessage(send_msg);
			}
			
		}else if(action.equals(Action.ACTION_READER_STARTED)){
			if(  (mReadCardActivity!=null)
			   &&(mReadCardActivity.getMainHandler()!=null)){
				send_msg.what=MainMsg.EVT_STARTED;
				mReadCardActivity.getMainHandler().sendMessage(send_msg);
			}
		}else if(action.equals(Action.ACTION_READER_READMODE_CHANGED))
		{
			if(  (mReadCardActivity!=null)
			   &&(mReadCardActivity.getMainHandler()!=null)){
				ReadMode rdMode = intent.getParcelableExtra(ActionKey.SERV_READCARD_NEW_READ_MODE);
				send_msg.what=MainMsg.EVT_READMODE_CHANGED_SERV;    				
				send_msg.obj=rdMode;
				mReadCardActivity.getMainHandler().sendMessage(send_msg);
			}			
		}else if(action.equals(Action.ACTION_READER_READTYPE_CHANGED))
		{
			if(  (mReadCardActivity!=null)
			   &&(mReadCardActivity.getMainHandler()!=null)){
				ReadType rdType = intent.getParcelableExtra(ActionKey.SERV_READCARD_NEW_READ_TYPE);
				send_msg.what=MainMsg.EVT_READTYPE_CHANGED_SERV;    				
				send_msg.obj=rdType;
				mReadCardActivity.getMainHandler().sendMessage(send_msg);
			}			
		}
	}
}
