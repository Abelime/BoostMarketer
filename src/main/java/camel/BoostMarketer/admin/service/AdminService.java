package camel.BoostMarketer.admin.service;

import camel.BoostMarketer.admin.dto.AdminDto;
import camel.BoostMarketer.admin.repository.MemoryAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemoryAdminRepository memoryAdminRepository;
    public void register(AdminDto adminDto) {

    }


}
