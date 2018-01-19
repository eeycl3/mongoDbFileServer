package com.blogpic.fileserver.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.blogpic.fileserver.model.File;
import com.blogpic.fileserver.service.FileService;
import com.blogpic.fileserver.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@CrossOrigin(origins = "*", maxAge = 3600)  // 允许所有域名访问
@Controller
public class FileController {

    @Autowired
    private FileService fileService;
    
    @Value("${server.port}")
    private String serverPort;
    
    @RequestMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("files", fileService.listFilesByPage(0,20)); 
        return "index";
    }

    /**
     * 分页查询文件
     * @param pageIndex
     * @param pageSize
     * @return
     */
	@GetMapping("files/{pageIndex}/{pageSize}")
    @ResponseBody
	public List<File> listFilesByPage(@PathVariable int pageIndex, @PathVariable int pageSize){
		return fileService.listFilesByPage(pageIndex, pageSize);
	}
			
    /**
     * 获取文件片信息
     * @param id
     * @return
     */
    @GetMapping("files/{id}")
    @ResponseBody
    public ResponseEntity<Object> serveFile(@PathVariable String id) {

        File file = fileService.getFileById(id);

        if (file != null) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream" )
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize()+"")
                    .header("Connection",  "close") 
                    .body( file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount");
        }

    }
    
    /**
     * 在线显示文件
     * @param id
     * @return
     */
    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Object> serveFileOnline(@PathVariable String id) {

        File file = fileService.getFileById(id);

        if (file != null) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType() )
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize()+"")
                    .header("Connection",  "close") 
                    .body( file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount");
        }

    }
    
    /**
     * upload
     * @param file
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        try {
        	File f = new File(file.getOriginalFilename(),  file.getContentType(), file.getSize(), file.getBytes());
        	f.setMd5( MD5Util.getMD5(file.getInputStream()) );
        	fileService.saveFile(f);
        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("message",
                    "Your " + file.getOriginalFilename() + " is wrong!");
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }
 
    /**
     * upload file
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
    	File returnFile = null;
        try {
        	File f = new File(file.getOriginalFilename(),  file.getContentType(), file.getSize(),file.getBytes());
        	f.setMd5( MD5Util.getMD5(file.getInputStream()) );
        	returnFile = fileService.saveFile(f);
        	String path = "//"+ "localhost" + ":" + serverPort + "/view/"+returnFile.getId();
        	return ResponseEntity.status(HttpStatus.OK).body(path);
 
        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
 
    }
    
    /**
     * delete file
     * @param id
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/delete")
    public String deleteFile(@RequestParam String id, RedirectAttributes redirectAttributes) {
 
    	try {
			fileService.removeFile(id);
		} catch (Exception e) {
            redirectAttributes.addFlashAttribute("message",
                    "Fail to delete file with id: " + id);
            return "redirect:/";
		}
        redirectAttributes.addFlashAttribute("message",
                "Succeed to delete file with id: " + id);
        return "redirect:/";
    }
}
