package com.maplestory.moewallpaperloader.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;



public class MultiThreadDownload 
{
	///< 下载文件路径【URL】
	private String path;
	///< 下载目标文件
	private String dstPath;
	///< 下载目标文件名称
	private String dstFileName;
	///< 目标文件父目录
	private String dstParentPath;
	///< 线程总数[默认为3】
	private int threadCount = 3;
	///< 缓冲区大小
	private int bufferLen = 1024 * 1024;
	///< 文件总大小
	private long size = 0;
	///< 当前文件大小【最后在onPorgress中会转换为百分百，当前下载字节数/总大小】
	private long currentSize = 0;
	// /< 多线程下载块文件大小
	private long blockSize;
	// /< 当前运行的线程数
	private int runningThreadCount;
	// /< 下载监听器
	private MultiThreadDownloadListenner mulDownLis;

	/**
	 * 传入下载的文件路径和线程数量
	 * @param _path
	 * @param _threadCount - 可以传-1，表示默认值3
	 */
	public MultiThreadDownload(String _path, String _dstPath, int _threadCount) 
	{
		this.path = _path;
		this.dstPath = _dstPath;
		this.dstFileName = _dstPath;
		String[] splits = this.dstFileName.split("/");
		this.dstFileName = splits[splits.length - 1];
		this.dstParentPath = _dstPath.substring(0, _dstPath.lastIndexOf("/")) + "/";
		if (_threadCount > 0)
		{
			this.threadCount = _threadCount;
		}
	}

	public void start()
	{
		try 
		{
			URL url = new URL(this.path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			int code = conn.getResponseCode();
			if (200 == code) 
			{
				/* long */size = conn.getContentLength(); ///< size = conn.getContentLengthLong();

				// /< 生成空白同样大小的文件
				blockSize = size / threadCount;
				File file = new File(this.dstPath);
				RandomAccessFile raf = new RandomAccessFile(file, "rw");
				raf.setLength(size);
				raf.close();
				//System.out.println("blockSize = " + blockSize);
				// /< 开启线程分别下载文件
				runningThreadCount = threadCount;
				for (int i = 1; i <= threadCount; ++i) 
				{
					long startPos = (i - 1) * blockSize;
					long endPos = i * blockSize - 1;
					if (i == threadCount) 
					{
						endPos = size - 1;
					}
					//System.out.println("startPos = " + startPos);
					//System.out.println("endPos = " + endPos);
					(new DownThread(this.path, i, startPos, endPos)).start();
				}
			}
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ProtocolException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDownThreadListenner(
			MultiThreadDownloadListenner _mulDownLis) 
	{
		mulDownLis = _mulDownLis;
	}

	/**
	 * 下载线程
	 * 
	 * @author hl
	 * 
	 */
	private class DownThread extends Thread 
	{
		///< 下载的文件路径
		private String path;
		///< 线程的ID号
		private int threadId;
		///< 下载的开始位置
		private long startPos;
		///< 下载的终止位置
		private long endPos;
		///< 线程需要下载的大小
		private long totalLen;

		public DownThread(String _path, int _threadId, 
						  long _startPos, long _endPos) 
		{
			this.path = _path;
			this.threadId = _threadId;
			this.startPos = _startPos;
			this.endPos = _endPos;
			this.totalLen = _endPos - _startPos + 1;
		}

		@Override
		public void run() 
		{
			try
			{
				// /< 读取上次下载的进度，重新计算本地下载的开始位置
				File positionFile = new File(dstParentPath + dstFileName + this.threadId + ".txt");
				// /< 当前下载线程的总大小
				long total = 0;
				// /< 如果有记录，则获取上次进度信息
				if (positionFile.exists() && positionFile.length() > 0)
				{
					FileInputStream fIn = new FileInputStream(positionFile);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(fIn));
					String lastTotal = br.readLine();
					long lastTotalInt = Long.valueOf(lastTotal);
					startPos += lastTotalInt; 	///< 下载位置重新计算
					///< 获取下载最大的位置
					currentSize += lastTotalInt;
					total += lastTotalInt; 		///< 下载后的总大小，基于上次下载
					br.close();
				}

				///< 创建URLConnection进行分段下载【设置分段参数】
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Range", "bytes=" + startPos + "-"
						+ endPos);
				conn.setConnectTimeout(5000);
				int code = conn.getResponseCode();
				///< 分段下载成功标志
				if (206 == code) 
				{
					///< 获取输入输出流
					InputStream inputS = conn.getInputStream();
					///< 创建本地文件
					File file = new File(dstPath);
					RandomAccessFile raf = new RandomAccessFile(file, "rw");
					///< 指定某个起始位置，开始写文件
					raf.seek(startPos);

					///< 实际读取的大小
					int len = 0;
					///< 写文件缓冲区大小1024 * 1024
					byte[] buffer = new byte[bufferLen];
					///< 实时记录下载的进度
					File recFile = new File(dstParentPath + dstFileName + this.threadId + ".txt");
					while ((len = inputS.read(buffer)) != -1) 
					{
						///< 创建实时写入文件流对象【实时刷新/硬盘/底层缓冲区】
						RandomAccessFile rf = new RandomAccessFile(recFile,
								"rwd");
						///< ******
						raf.write(buffer, 0, len);
						///< ******
						
						///< 获取当前下载字节数 , 并通知客户进度  
						synchronized (ItemAdapter.class) 
						{
							currentSize += len;
							//System.out.println("ssss " + currentSize + " " + size);
							mulDownLis.onProgress(currentSize, currentSize/(float)size);
						}
						
						///< 实时记录文件写入的字节数
						total += len;
						rf.write(String.valueOf(total).getBytes());
						rf.close();
					}
					inputS.close();
					raf.close();
				}
			} 
			catch (MalformedURLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (ProtocolException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
				try 
				{
					throw new DownThreadException("线程下载异常");
				}
				catch (DownThreadException e1)
				{
					// TODO Auto-generated catch block
					// e1.printStackTrace();
				}
			}
			finally 
			{
				///< 保证所有线程结束之后删除记录文件
				synchronized (ItemAdapter.class) 
				{
					///< 如果出现异常，以实际下载的大小作为判断下载是否执行完成的条件
					if (totalLen == getRecLen(dstParentPath + dstFileName + this.threadId + ".txt"))
					{
						//System.out.println("线程" + threadId + " 下载完毕！");
						runningThreadCount--;
						if (runningThreadCount < 1 
							&& size == getToalLen(threadCount)) 
						{
							for (int i = 1; i <= threadCount; ++i)
							{
								File file = new File(dstParentPath + dstFileName + i + ".txt");
								file.delete();
							}
							try
							{
								throw new DownThreadException("DONE");
							} 
							catch (DownThreadException e1) 
							{
								// TODO Auto-generated catch block
								// e1.printStackTrace();
								///< 什么都不做
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 【通过下载日志记录文件】获取单个线程下载的大小
	 * 
	 * @param file
	 * @return
	 */
	private long getRecLen(String file) 
	{
		///< 下载总大小
		long lastTotalInt = 0;
		///< 读取上次下载的进度，重新计算本地下载的开始位置
		File positionFile = new File(file);
		///< 如果有记录，则获取上次进度信息
		if (positionFile.exists() && positionFile.length() > 0) 
		{
			try 
			{
				FileInputStream fIn = new FileInputStream(positionFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fIn));
				String lastTotal = br.readLine();
				lastTotalInt = Long.valueOf(lastTotal);
				br.close();
			} 
			catch (NumberFormatException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lastTotalInt;
	}

	/**
	 * 【通过下载日志记录文件】获取所有线程已经下载的总和
	 * 
	 * @param threadCount
	 * @return
	 */
	private long getToalLen(int threadCount) 
	{
		///< 下载总大小
		long total = 0;
		for (int i = 1; i <= threadCount; ++i) 
		{
			///< 读取上次下载的进度，重新计算本地下载的开始位置
			File positionFile = new File(this.dstParentPath + this.dstFileName + i + ".txt");
			///< 如果有记录，则获取上次进度信息
			if (positionFile.exists() && positionFile.length() > 0) 
			{
				try 
				{
					FileInputStream fIn = new FileInputStream(positionFile);
					BufferedReader br = new BufferedReader(new InputStreamReader(fIn));
					String lastTotal = br.readLine();
					long lastTotalInt = Long.valueOf(lastTotal);
					///< 下载后的总大小，基于上次下载
					total += lastTotalInt;
					br.close();
				} 
				catch (NumberFormatException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (FileNotFoundException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return total;
	}

	/**
	 * 自定义异常处理 【 1.当下载异常时或者未完成时不处理异常，避免用户体验；
	 * 2.当下载完成时，则标记此任务结束，类似迅雷，可以选择重新下载；其它慢慢完善 】
	 * 
	 * @author hl
	 * 
	 */
	private class DownThreadException extends Exception 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DownThreadException(String msg) 
		{
			super(msg);

		}
	}
}
