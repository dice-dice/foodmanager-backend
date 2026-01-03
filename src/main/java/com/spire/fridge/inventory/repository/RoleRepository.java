package com.spire.fridge.inventory.repository;

import com.spire.fridge.inventory.entity.ERole;
import com.spire.fridge.inventory.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
