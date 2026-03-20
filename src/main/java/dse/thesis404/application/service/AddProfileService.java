package dse.thesis404.application.service;

import dse.thesis404.infrastructure.web.form.ProfileForm;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AddProfileService {

    private final List<ProfileForm> profiles = new ArrayList<>();

    public void addProfile(ProfileForm profileForm) {
        profiles.add(profileForm);
    }

    public List<ProfileForm> getAllProfiles() {
        return profiles;
    }
}

