package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(long userId);

    List<ItemRequest> findAllByRequestorIdIsNot(Long requestorId, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.request.id = :requestId")
    List<ItemDtoOut> findItemsByRequestId(@Param("requestId") Long requestId);

}
