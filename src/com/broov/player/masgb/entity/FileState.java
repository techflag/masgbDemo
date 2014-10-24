package com.broov.player.masgb.entity;



import android.os.Parcel;
import android.os.Parcelable;

public class FileState implements Parcelable
{
	private String fileName;//文件名字
	private String url;//下载地址
	
	public FileState()
	{
		
	}
	

	
	
	public FileState(String fileName, String url )
	{
		super();
		this.fileName = fileName;
		this.url = url;
	}
	
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation 
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "    ";
	    
	    String retValue = "";
	    
	    retValue = "FileState ( "
	        + super.toString() + TAB
	        + "musicName = " + this.fileName + TAB
	        + "url = " + this.url + TAB
	        + " )";
	
	    return retValue;
	}
	
	public static final Parcelable.Creator<FileState> CREATOR =
		new Parcelable.Creator<FileState>()
		{

			public FileState createFromParcel(Parcel in)
			{
				FileState fileState = new FileState();
				fileState.fileName=in.readString();
				fileState.url=in.readString();
				return fileState;
			}

			public FileState[] newArray(int size)
			{
				
				return new FileState[size];
			}
		
		};
	
	//这个方法不用管
	public int describeContents() 
	{
		return 0;
	}
	public void writeToParcel(Parcel parcel, int flags)
	{
		parcel.writeString(fileName);
		parcel.writeString(url);
	}

	
	
	
	
}
