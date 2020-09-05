package com.xiaobai.blog.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiaobai.blog.bean.YellowCalendar;
import com.xiaobai.blog.bean.Category;
import com.xiaobai.blog.bean.Content;
import com.xiaobai.blog.service.categoryService;
import com.xiaobai.blog.service.contentService;
import com.xiaobai.blog.staticlass.UrlClass;
import com.xiaobai.blog.until.JsoupUntils;
import com.xiaobai.blog.until.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ServiceController {
    @Autowired
    UploadFile uploadFile;
    @Autowired
    contentService contentService;
    @Autowired
    categoryService categoryService;
    @Autowired
    JsoupUntils jsoupUntils;
    @RequestMapping("/edit")
    public String edit(Model model) {
        Collection<Category> categories = categoryService.getallCategory();
        model.addAttribute("category",categories);
        HashSet<String> set=new HashSet<>();
        set.add("获取出错-哭");
        Calendar cl=Calendar.getInstance();
        YellowCalendar yellowCalendar =new YellowCalendar("今天公历  "+cl.get(Calendar.YEAR)+"年"+(cl.get(Calendar.MONTH)+1)+"月"+cl.get(Calendar.DAY_OF_MONTH)+"日","",set, set,"获取出错-哭");
        YellowCalendar document = jsoupUntils.getDocument(UrlClass.url);
        if (document==null){
            document= yellowCalendar;
        }
        model.addAttribute("calendar",document);
        return "main/release";
    }
    @PostMapping("/search")
    public String search(Model model,HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        String search = request.getParameter("search");
        Collection<Category> categories=categoryService.getallCategory();
        model.addAttribute("category",categories);
        Collection<Content> contents = contentService.getsearchcontent(search);
        model.addAttribute("content",contents);
        HashSet<String> set=new HashSet<>();
        set.add("获取出错-哭");
        Calendar cl=Calendar.getInstance();
        YellowCalendar yellowCalendar =new YellowCalendar("今天公历  "+cl.get(Calendar.YEAR)+"年"+(cl.get(Calendar.MONTH)+1)+"月"+cl.get(Calendar.DAY_OF_MONTH)+"日","",set, set,"获取出错-哭");
        YellowCalendar document = jsoupUntils.getDocument(UrlClass.url);
        if (document==null){
            document= yellowCalendar;
        }
        model.addAttribute("calendar",document);
        return "main/search";
    }
    @PostMapping("/uploadImage")
    public void uploadImage(@RequestParam("file") MultipartFile[] file,HttpServletResponse response) throws IOException {
        JSONObject jsonObject=new JSONObject();
        String[] uploadfile = uploadFile.uploadfile(file, "images/");
        JSONObject json=new JSONObject();
        String[] data=new String[file.length];
        if (uploadfile!=null){
            data=uploadfile;
            json.put("data",data);
            json.put("errno",0);
        }
        else{
            json.put("data",data);
            json.put("errno",1);
            json.put("msg","图片保存失败");
        }
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        out.print(json);
    }

    @PostMapping("/saveContent")
    public void saveContent(@RequestParam("catenew") String cate, Content content,HttpServletResponse response) throws IOException {
        if (!"".equals(cate) &&cate!=null){
            Integer select = categoryService.select(cate);
            if (select==null){
                Category category=new Category(null,cate);
                categoryService.insertCategory(category);
                content.setCategory(category.getID());
            }
            else {
                content.setCategory(select);
            }
        }
        JSONObject jsonObject=new JSONObject();
        content.setTime(new Date());

        Matcher m = Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(content.getTextContent());
        while(m.find()) {
            content.setImageUri(m.group(1));
            break;
        }
        System.out.println(content);
        if(contentService.insertcontent(content)!=0){
                jsonObject.put("status",0);
                jsonObject.put("msg","保存成功了哦~");
        }
        else{
            jsonObject.put("status",1);
            jsonObject.put("msg","保存失败了哦~");
        }
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        out.print(jsonObject);

    }

    @GetMapping("/detail")
    public String detail(@RequestParam("id") Integer id, Model model){
        Content getcontent = contentService.getcontent(id);
        model.addAttribute("content",getcontent);
        HashSet<String> set=new HashSet<>();
        set.add("获取出错-哭");
        Calendar cl=Calendar.getInstance();
        YellowCalendar yellowCalendar =new YellowCalendar("今天公历  "+cl.get(Calendar.YEAR)+"年"+(cl.get(Calendar.MONTH)+1)+"月"+cl.get(Calendar.DAY_OF_MONTH)+"日","",set, set,"获取出错-哭");
        YellowCalendar document = jsoupUntils.getDocument(UrlClass.url);
        if (document==null){
            document= yellowCalendar;
        }
        model.addAttribute("calendar",document);
        return "main/detailphoto";
    }

    @PostMapping("/delete")
    public void detele(@RequestParam("id") Integer id,HttpServletResponse response) throws IOException {
        JSONObject jsonObject=new
                JSONObject();

        Boolean deletecontent = contentService.deletecontent(id);
        if (deletecontent){
            jsonObject.put("msg","删除成功！");
        }
        else{
            jsonObject.put("msg","删除失败！");
        }
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        out.print(jsonObject);

    }
    @PostMapping("/deleteCategory")
    public void deleteCategory(@RequestParam("id") Integer id,HttpServletResponse response) throws IOException {
        JSONObject jsonObject=new
                JSONObject();
        Boolean deletecontent = categoryService.deleteCategory(id);
        Integer integer = categoryService.deleteAll(id);
        if (deletecontent){
            jsonObject.put("status",0);
            jsonObject.put("msg","删除成功！");
        }
        else{
            jsonObject.put("status",1);
            jsonObject.put("msg","删除失败！重新选择试试！");
        }
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        out.print(jsonObject);

    }
}
