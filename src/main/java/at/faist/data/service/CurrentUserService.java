package at.faist.data.service;

import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public String getCurrentUser() {
        return "demo";
    }
}
