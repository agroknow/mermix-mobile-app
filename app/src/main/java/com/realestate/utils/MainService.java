package com.realestate.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.model.common.Pojo;
import com.realestate.utils.net.RestApiConsumer;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created on 14/07/2015
 * Description:
 * service class used to execute long-running operation.
 * in method onStartCommand, asyncTask is initialized and its execute is invoked
 * when asyncTask finishes execution, method asyncTaskCB is invoked
 */
public class MainService extends Service {
	private Class<? extends Pojo> pojoClass;
	private Class<? extends Pojo[]> pojoArrayClass;
	private String pojoClassName;
	public static final String SERVICEACTION = "ServiceAction";
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		if(intent != null){
			new RestApiConsumer(this).execute(intent.getExtras().getString(Constants.INTENTVARS.APIURL));
			this.pojoClassName = intent.getExtras().getString(Constants.INTENTVARS.POJOCLASS);
		}
		else{
			Common.logError("MainService onStartCommand intent is null");
		}
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void asyncTaskCB(JsonNode asyncTaskResult){
		Common.log("MainService asyncTaskCB");
		ObjectMapper mapper = JacksonObjectMapper.getInstance();
		Pojo apiResponseData = null;
		try{
			if(asyncTaskResult != null){
				/*
				 * use parser in order to detect error response
				 * and if NO error, then traverse JSON response with POJO class
				 * NO re-parsing possible
				 */
				JsonParser parserResult = mapper.treeAsTokens(asyncTaskResult);
				JsonToken nextToken = parserResult.nextToken();
				if (nextToken != JsonToken.START_OBJECT && nextToken != JsonToken.START_ARRAY) {
					Common.logError(Constants.ErrorMessages.NO_OBJECT_IN_DATA);
				}
				else if(nextToken.equals(JsonToken.START_OBJECT)){
					this.pojoClass = (Class<? extends Pojo>) Class.forName(this.pojoClassName);
					apiResponseData = mapper.readValue(parserResult, this.pojoClass);
					broadCastData(apiResponseData);
				}
				else if(nextToken.equals(JsonToken.START_ARRAY)){
					this.pojoArrayClass = (Class<? extends Pojo[]>) Class.forName("[L"+this.pojoClassName+";");
					Pojo[] apiResponseArrayData = mapper.readValue(parserResult, this.pojoArrayClass);
					broadCastArrayData(apiResponseArrayData);
				}
				parserResult.close();
			}
			else{
				//Common.logError(Constants.ErrorMessages.NO_DATA);
				broadCastData(apiResponseData);
			}
		} catch (JsonParseException e){
			Common.logError("JsonParseException @ asyncTaskCB:" + e.getMessage());
			//e.printStackTrace();
		} catch (JsonMappingException e){
			Common.logError("JsonMappingException @ asyncTaskCB:" + e.getMessage());
			//e.printStackTrace();
		} catch (JsonProcessingException e){
			Common.logError("JsonProcessingException @ asyncTaskCB:" + e.getMessage());
			//e.printStackTrace();
		} catch (IOException e){
			Common.logError("IOException @ asyncTaskCB:" + e.getMessage());
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Common.logError("ClassNotFoundException @ asyncTaskCB:" + e.getMessage());
			//e.printStackTrace();
		}

	}

	private void broadCastArrayData(Pojo[] pojoData) {
		Common.log("MainService broadCastArrayData");
		Intent intentBroadCast = new Intent();
		intentBroadCast.setAction(SERVICEACTION);
		if(pojoData.length == 0){
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("APIRESPONSEDATA", (Serializable) null);
			intentBroadCast.putExtras(mBundle);
			sendBroadcast(intentBroadCast);
		}
		else{
			for(int idx=0;idx<pojoData.length;idx++){
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("APIRESPONSEDATA", (Serializable) pojoData[idx]);
				intentBroadCast.putExtras(mBundle);
				sendBroadcast(intentBroadCast);
			}
		}
	}

	private void broadCastData(Pojo pojoData){
		Common.log("MainService broadCastData");
		Intent intentBroadCast = new Intent();
		intentBroadCast.setAction(SERVICEACTION);
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("APIRESPONSEDATA", (Serializable) pojoData);
		intentBroadCast.putExtras(mBundle);
		sendBroadcast(intentBroadCast);
	}
}
