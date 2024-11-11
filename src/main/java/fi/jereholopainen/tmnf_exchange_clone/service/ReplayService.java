package fi.jereholopainen.tmnf_exchange_clone.service;

import org.springframework.web.multipart.MultipartFile;

public interface ReplayService {
    void saveReplay(MultipartFile file, Long trackId);
}
