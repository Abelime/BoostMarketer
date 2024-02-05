package camel.BoostMarketer.admin.repository;

import camel.BoostMarketer.admin.dto.AdminDto;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MemoryAdminRepository {

    private static Map<Long, AdminDto> member = new HashMap<>();

    private static long sequence = 0l;

}
