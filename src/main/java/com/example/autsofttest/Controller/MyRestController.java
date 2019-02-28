package com.example.autsofttest.Controller;

import com.example.autsofttest.Persistence.Registry;
import com.example.autsofttest.Persistence.Category;
import com.example.autsofttest.Persistence.CategoryRepository;
import com.example.autsofttest.Persistence.RegistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.lang.reflect.Array;
import java.util.*;

@RestController
public class MyRestController {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    RegistryRepository registryRepository;


    @RequestMapping(value="/init")
    public boolean init(){
        categoryRepository.deleteAll();
        registryRepository.deleteAll();
        for (String cat : new ArrayList<String>(){{add("SCI");add("PRIV");add("SPORT");add("INL");add("OUTL");}})
        {
            createCategory(cat);
        }
        createRegistry("Vadludak költési ciklusa",
                "Mint ismeretes a vadludak költöző madarak. Megismerhetőek .....",
                new ArrayList<String>(){{add("madár");add("lúd");add("természet");}});
        createRegistry("Windu kardja a boltokban",
                "A StarTech startup élethű másolatot gyártott a Samuel L Jackson által alakított....",
                new ArrayList<String>(){{add("starwars");add("fénykard");add("innováció");}});
        createRegistry("Kitüntetést kapott Lovász Elemér",
                "Híres úszó paralimpikonunk, Lovász Elemér kitüntetést vehetett át ....",
                new ArrayList<String>(){{add("úszás");add("paralimpikon");add("kitüntetés");}});
        return true;
    }

    @RequestMapping(value = "/createRegistry", method= RequestMethod.POST)
    public Registry createRegistry(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("labelList") List<String> labelList){
        Registry result = new Registry(title,content,labelList);
        registryRepository.save(result);
        return result;
    }
    @RequestMapping(value = "/modifyRegistry/{registryId}", method= RequestMethod.POST)
    public Registry modifyRegistry(@PathVariable("registryId") Integer Id, @RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("labelList") List<String> labelList){
        Registry result = registryRepository.findById(Id).get();
        if (result==null){
            return null;
        }
        result.setTitle(title);
        result.setContent(content);
        result.setLabels(labelList);
        result.setModifyDate(new Date());

        registryRepository.save(result);
        return result;
    }

    @RequestMapping(value = "/deleteRegistry/{registryId}", method= RequestMethod.GET)
    public boolean deleteRegistry(@PathVariable("registryId") Integer Id){
        Optional<Registry> deletableRegistry = registryRepository.findById(Id);
        if (!deletableRegistry.isPresent()){
            return false;
        }
        registryRepository.delete(deletableRegistry.get());
        return true;
    }



    @RequestMapping(value = "/createCategory", method= RequestMethod.POST)
    public Category createCategory(@RequestParam("name") String name){
        Category result = new Category(name);
        categoryRepository.save(result);
        return result;
    }

    @RequestMapping(value = "/deleteCategory/{categoryId}", method= RequestMethod.GET)
    public boolean deleteCategory(@PathVariable("categoryId") Integer Id){
        Optional<Category> deletableCategory = categoryRepository.findById(Id);
        if (!deletableCategory.isPresent()){
            return false;
        }
        categoryRepository.delete(deletableCategory.get());
        return true;
    }

    @RequestMapping(value = "/assignCategory/{categoryId}/{registryId}")
    public boolean assignCategoryToRegistry(@PathVariable("categoryId") Integer categoryId, @PathVariable("registryId") Integer registryId){
        Optional<Category> assignableCategory = categoryRepository.findById(categoryId);
        Optional<Registry> assignableRegistry = registryRepository.findById(registryId);
        if (!assignableCategory.isPresent() || !assignableRegistry.isPresent()){
            return false;
        }
        Category modifiableCategory = assignableCategory.get();
        if (modifiableCategory.getRegistries().contains(assignableRegistry.get())){
            return false;
        }
        modifiableCategory.addRegistry(assignableRegistry.get());
        categoryRepository.save(modifiableCategory);
        return true;
    }
    @RequestMapping(value = "/deleteAssignment/{categoryId}/{registryId}")
    public boolean deleteAssignment(@PathVariable("categoryId") Integer categoryId, @PathVariable("registryId") Integer registryId){
        Optional<Category> unassignableCategory = categoryRepository.findById(categoryId);
        Optional<Registry> unassignableRegistry = registryRepository.findById(registryId);
        if (!unassignableCategory.isPresent() || !unassignableRegistry.isPresent()){
            return false;
        }
        Registry modifiableRegistry = unassignableRegistry.get();
        modifiableRegistry.deleteCategory(unassignableCategory.get());
        registryRepository.save(modifiableRegistry);
        return true;
    }

    @RequestMapping(value = "/listRegistries/{categoryId}")
    public List<Registry> listRegistries(@PathVariable("categoryId") Integer categoryId, @RequestParam Integer page, @RequestParam Integer pageSize) {
        Optional<Category> actualCategory = categoryRepository.findById(categoryId);
        if (!actualCategory.isPresent()) {
            return null;
        }

        List<Registry> registries = registryRepository.findAllByCategoriesContaining(actualCategory.get());
        Collections.sort(registries);
        return registries.subList((page-1)*pageSize,page*pageSize);
    }


    @RequestMapping(value = "/listRegistriesByCategoryName/{categoryName}")
    public List<Registry> listRegistriesByCategoryName(@PathVariable("categoryName") String categoryName){
        Set<Registry> resultSet = new HashSet();
        for (Category category : categoryRepository.findByNameContaining(categoryName)){
            for (Registry registry : category.getRegistries()){
                resultSet.add(registry);
            }
        }
        List<Registry> resultList = new ArrayList<Registry>();
        resultList.addAll(resultSet);
        return resultList;
    }

    @RequestMapping(value = "/listRegistriesByLabel/{labelName}")
    public List<Registry> listRegistriesByLabelName(@PathVariable("labelName") String labelName){
        List<Registry> result = new ArrayList<>();
        for (Registry registry : registryRepository.findAll()){
            for (String label : registry.getLabels()){
                if (label.equals(labelName)){
                    result.add(registry);
                }
            }
        }
        return result;
    }
}
