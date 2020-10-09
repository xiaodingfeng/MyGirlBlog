package com.xiaobai.blog.controller;

import com.xiaobai.blog.bean.YellowCalendar;
import com.xiaobai.blog.bean.Category;
import com.xiaobai.blog.bean.Content;
import com.xiaobai.blog.service.categoryService;
import com.xiaobai.blog.service.contentService;
import com.xiaobai.blog.service.userService;
import com.xiaobai.blog.staticlass.UrlClass;
import com.xiaobai.blog.until.JsoupUntils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class LoginController {
    @Autowired
    contentService contentService;
    @Autowired
    categoryService categoryService;
    @Autowired
    userService userService;
    @Autowired
    JsoupUntils jsoupUntils;
    @GetMapping("/")
    public String main(Model model){
        Collection<Content> textContent=contentService.getallcontent(0,null);
        List<Content> contents = new ArrayList<>(textContent);
        String regex = "<\\s*img(.+?)src=[\"'](.*?)[\"']\\s*/?\\s*>";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        for (Content content : contents) {
            Matcher matcher = pattern.matcher(content.getTextContent());
            while (matcher.find()) {
                content.setTextContent(content.getTextContent().replaceAll(matcher.group(0),""));
            }
        }
        model.addAttribute("textContentAll",contents);
        int index=contents.size()>9?8:contents.size()-1;
        model.addAttribute("textContent",contents.subList(0,index));

        Collection<Category> categories=categoryService.getallCategory();
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
        return "main/index";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

}
