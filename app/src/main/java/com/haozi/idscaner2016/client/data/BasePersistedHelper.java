package com.haozi.idscaner2016.client.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.haozi.idscaner2016.common.app.MyApp;
import com.haozi.idscaner2016.common.utils.StringUtil;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Set;

/**
 * 基础数据配置保存接口
 * @author yinhao
 * @功能 主要用于存储APP配置和用户基础信息
 * @创建日期 2015年10月8日 上午9:55:43
 * @备注 [修改者，修改日期，修改内容]
 * */
public class BasePersistedHelper {
	
	protected static String NAME = "BasePersistedHelper";
	private final SharedPreferences mDB;
	private static final BasePersistedHelper INSTANCE = new BasePersistedHelper();

	public static BasePersistedHelper getInstance() {
		return INSTANCE;
	}

	protected BasePersistedHelper(String name) {
		NAME = name;
		mDB = MyApp.getInstance().getSharedPreferences(name,Context.MODE_PRIVATE);
	}
	
	protected BasePersistedHelper() {
		mDB = MyApp.getInstance().getSharedPreferences(NAME,Context.MODE_PRIVATE);
	}

	public SharedPreferences getDB() {
		return mDB;
	}

	/**
	 * Save KeyvaluePair
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public <T> void save(String key, T value) {
		if (StringUtil.isEmpty(key) || value == null) {
			return;
		}
		Editor edit = mDB.edit();
		if (Boolean.class.isInstance(value)) {
			edit.putBoolean(key, (Boolean) value);
		} else if (Integer.class.isInstance(value)) {
			edit.putInt(key, (Integer) value);
		} else if (Float.class.isInstance(value)) {
			edit.putFloat(key, (Float) value);
		} else if (Long.class.isInstance(value)) {
			edit.putLong(key, (Long) value);
		} else if (String.class.isInstance(value)) {
			try {
				String str = URLEncoder.encode((String) value, "UTF-8");
				edit.putString(key, str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (Set.class.isInstance(value)) {
			edit.putStringSet(key, (Set<String>) value);
		}else if(BigDecimal.class.isInstance(value)){
			edit.putString(key, value.toString());
		}
		edit.commit();
	}

	public int getInt(String key) {
		return mDB.getInt(key, -1);
	}

	public long getLong(String key) {
		return mDB.getLong(key, -1);
	}

	public boolean getBoolean(String key) {
		return mDB.getBoolean(key, false);
	}

	public BigDecimal getBigDecimal(String key) {
		String value = getString(key);
		if(StringUtil.isEmpty(value)){
			return null;
		}
		return new BigDecimal(value);
	}

	public String getString(String key) {
		try {
			if(mDB.getString(key, null) == null){
				return null;
			}
			String strSave = URLDecoder.decode(mDB.getString(key, null), "UTF-8");
			return strSave;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
    public void putString(String key, String value) {
        if(mDB != null) {
        	Editor etor = mDB.edit();
            etor.putString(key, value);
            etor.commit();
        }
    }
	
    public void putLong(String key, long value) {
        if(mDB != null) {
        	Editor etor = mDB.edit();
            etor.putLong(key, value);
            etor.commit();
        }
    }
    
    public void putDouble(String key, double value){
    	 if(mDB != null) {
         	Editor etor = mDB.edit();
             etor.putString(key, String.valueOf(value));
             etor.commit();
         }
    }
    
    public double getDouble(String key){
    	try {
			if(mDB.getString(key, null) == null){
				return 0;
			}
			String strSave = URLDecoder.decode(mDB.getString(key, null), "UTF-8");
			return Double.valueOf(strSave);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
    }
    
    
    public void putBoolean(String key, boolean value){
    	 if(mDB != null) {
         	Editor etor = mDB.edit();
             etor.putBoolean(key, value);
             etor.commit();
         }
    }
    
    public boolean getBoolean(String key,boolean defaultValue){
		return mDB.getBoolean(key, defaultValue);
    }
    
	public void clearSP() {
		Editor etor = mDB.edit();
		etor.clear();
		etor.commit();
	}
}
