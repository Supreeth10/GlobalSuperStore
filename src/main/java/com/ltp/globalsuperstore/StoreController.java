package com.ltp.globalsuperstore;
import java.util.Date;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ltp.globalsuperstore.Constants.*;

@Controller
public class StoreController {
    private List<Item> items = new ArrayList<Item>();
    @GetMapping("/")
    public String getForm(Model model, @RequestParam(required = false) String id){
        int index = checkItem(id);
        model.addAttribute("item",index== NOT_FOUND ? new Item() : items.get(index)); ;
        model.addAttribute("categories", Constants.CATEGORIES);
        return "form";
    }

    @GetMapping("/inventory")
    public String getInventory(Model model){
        model.addAttribute("list",items);
        return "inventory";
    }

    @PostMapping("/submitItem")
    public String handleSubmit(Item item, RedirectAttributes redirectAttributes) {
        int index = checkItem(item.getId());
        String status = SUCCESS_STATUS;
        if(index == NOT_FOUND){
            items.add(item);
        }else if (within5Days(item.getDate(), items.get(index).getDate())){
            items.set(index, item);
        }else{
            status = FAILED_STATUS;
        }

        redirectAttributes.addFlashAttribute("status", status);
        return "redirect:/inventory";

    }

    public int checkItem(String id) {
        for(int i=0;i<items.size();i++) {
            if(items.get(i).getId().equals(id)) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    public boolean within5Days(Date newDate, Date oldDate) {
        long diff = Math.abs(newDate.getTime() - oldDate.getTime());
        return (int) (TimeUnit.MILLISECONDS.toDays(diff)) <= 5;
    }



}
