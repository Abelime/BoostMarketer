package camel.BoostMarketer.blog.repository;

import camel.BoostMarketer.blog.dto.BlogDto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MemoryBlogRepository {

    private static final Map<Long, BlogDto> blogMap = new HashMap<>();

    private static long sequence = 0L;

    public void save(BlogDto blogDto){
        blogMap.put(++sequence, blogDto);
    }

    public List<BlogDto> findAll(){
        return new ArrayList<>(blogMap.values());
    }
    

}
