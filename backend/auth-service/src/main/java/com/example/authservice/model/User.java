package com.example.authservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.*;

@Entity
@Data
@Table(name = "USERS")
@SQLDelete(sql = "UPDATE USERS SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
public abstract class User implements UserDetails {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(nullable = false)
    private boolean passwordSet = false;

    private String password;

    @Column(unique = true)
    private String verificationCode;

    @Column(nullable = false)
    private String twoFactorKey;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String city;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    public Role getRole() {
        return this.roles.get(0);
    }

    public void setRole(Role role) {
        if(this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(role);
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> permissions = new ArrayList<>(20);
        for (Role role : this.roles) {
            permissions.add(role);
            permissions.addAll(role.getPrivileges());
        }
        return permissions;
    }

    @Column(nullable = false)
    private Integer loginAttempts = 0;

    @Column(nullable = false)
    private Instant lastLoginAttempt = Instant.MIN;

    @Column(nullable = false)
    private boolean deleted = Boolean.FALSE;

    @Column(nullable = false)
    private Instant lockedUntil = Instant.MAX;

    private String lockReason;

    @CreationTimestamp
    private Instant created;

    @UpdateTimestamp
    private Instant modified;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public boolean isLocked() {
        return Instant.now().isBefore(lockedUntil);
    }

    public void lockAccount(int duration, TemporalUnit temporalUnit, String reason) {
        setLockedUntil(Instant.now().plus(duration, temporalUnit));
        setLockReason(reason);
    }

    public void lockAccount(String reason) {
        setLockedUntil(Instant.parse("9999-12-31T23:59:59Z"));
        setLockReason(reason);
    }

    public void unlockAccount() {
        setLockedUntil(Instant.parse("1111-12-31T23:59:59Z"));
        setLockReason(null);
    }
}
