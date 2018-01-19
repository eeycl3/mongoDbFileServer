/**
 * 
 */
package com.blogpic.fileserver.service;

import java.util.List;

import com.blogpic.fileserver.model.File;

public interface FileService {
	/**
	 * 保存文件
	 * @param file
	 * @return
	 */
	File saveFile(File file);
	
	/**
	 * 删除文件
	 * @param id
	 * @return
	 */
	void removeFile(String id);
	
	/**
	 * 根据id获取文件
	 * @param id
	 * @return
	 */
	File getFileById(String id);

	/**
	 * 分页查询，按上传时间降序
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	List<File> listFilesByPage(int pageIndex, int pageSize);
}
