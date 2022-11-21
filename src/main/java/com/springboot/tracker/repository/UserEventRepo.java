package com.springboot.tracker.repository;


import com.springboot.tracker.entity.Event;
import com.springboot.tracker.entity.UserEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface UserEventRepo extends JpaRepository<UserEvents,Long> {

    boolean existsById(Long aLong);

    @Transactional
    @Modifying
    @Query("DELETE from UserEvents WHERE user.id=:user_id and uEvent.event_id=:event_id")
    void cancelEventRegistration(long user_id, long event_id);

    @Query("SELECT e.user.id FROM UserEvents e WHERE e.uEvent.event_id<=:event_id and e.delivered=:delivered")
    List<Long> getUsers(long event_id, boolean delivered);

    @Transactional
    @Modifying
    @Query("update UserEvents e set e.delivered =:delivered WHERE e.user.id=:userId and e.uEvent.event_id=:event_id")
    void updateUserEvent(boolean delivered, long userId, long event_id);

    @Query("SELECT e.user.id from UserEvents e where e.uEvent.event_id=:event_id")
    List<Long> getUserForEvent(long event_id);
    @Query("SELECT e.id from UserEvents e where e.user.id=:user_id and e.uEvent.event_id=:event_id")
    Long checkRegistrationDetails(long user_id, long event_id);

    @Query("SELECT e.uEvent from UserEvents e where e.user.id=:user_id")
    List<Event> getAllEventsForUsers(long user_id);




}

