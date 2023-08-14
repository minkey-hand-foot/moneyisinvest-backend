package org.knulikelion.moneyisinvest.service.impl;

import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }
}
