package com.game.log.logback;

import ch.qos.logback.core.FileAppender;
import com.game.util.ExceptionUtils;
import com.game.util.RootLogUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * 写日志类
 * @author liguorui
 *
 */
public class DailyFileAppender<E> extends FileAppender<E> {

	/**
	 * 原文件名
	 */
	private String oriFileName;

	private LocalDate laseDate;

	private static final String dataPattern = "yyyy-MM-dd";

	private Object rollLockObject = new Object();

	/**
	 * 父目录创建锁
	 */
	private static Object parentDirectoryCreateObject = new Object();

	private File file;

	@Override
	public void start() {
		if (this.oriFileName == null) { //框架调用，没有设置
			this.oriFileName = fileName;
		}
		this.laseDate = LocalDate.now();
		String formatStr = laseDate.toString(dataPattern);
		this.fileName = getRealFileName(formatStr) + "." + formatStr;
		super.start();
		this.file = new File(fileName);
		if (!isStarted()) {
			ExceptionUtils.log("{} 日志文件启动失败", oriFileName);
		}
	}

	@Override
	protected void subAppend(E event) {
		if (!isSameDate()) {
			synchronized (rollLockObject) {
				rollOver();
			}
		}
		if (!file.exists()) {
			ExceptionUtils.log("日志文件{} 不存在", fileName);
			try {
				openFile(fileName);
			} catch (IOException e) {
				ExceptionUtils.log(e);
			}
		}
		super.subAppend(event);
	}

	private boolean isSameDate() {
		LocalDate now = LocalDate.now();
		return now.isEqual(laseDate);
	}

	private void rollOver() {
		LocalDate now = LocalDate.now();
		if (now.isEqual(laseDate)) {
			return;
		}
		this.laseDate = now;
		lock.lock();;
		try {
			String formatStr = laseDate.toString(dataPattern);
			this.fileName = getRealFileName(formatStr) + "." + formatStr;
			createMissingParentDirectories(new File(fileName));
			openFile(fileName);
			this.file = new File(fileName);
			RootLogUtils.infoLog("生成文件 {}", fileName);
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionUtils.log(e);
		} finally {
			lock.unlock();
		}
	}

	private void createMissingParentDirectories(File file) {
		File paret = file.getParentFile();
		if (paret == null) {
			return;
		}
		if (paret.exists()) {
			return;
		}
		synchronized (parentDirectoryCreateObject) {
			if (paret.exists()) {
				return;
			}
			paret.mkdirs();
		}
	}

	private String getRealFileName(String formatStr) {
		File file = new File(oriFileName);
		String path = file.getParent();
		String name = file.getName();
		if (path != null) {
			return path + File.separator + formatStr + File.separator + name;
		} else {
			return formatStr + File.separator + name;
		}
	}

	public String getOriFileName() {
		return oriFileName;
	}

	public void setOriFileName(String oriFileName) {
		this.oriFileName = oriFileName;
	}
}
