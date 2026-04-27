package com.bibliotech.repository;

import com.bibliotech.db.JsonDatabase;
import com.bibliotech.model.Member;
import com.bibliotech.model.UserSearchCriteria;
import java.util.List;
import java.util.stream.Collectors;

public class JsonMemberRepository extends JsonRepository<Member, String> implements MemberRepository {
    public JsonMemberRepository(JsonDatabase db) {
        super(db, db::getMembers);
    }

    @Override
    public List<Member> search(UserSearchCriteria criteria) {
        return findAll().stream()
            .filter(m -> criteria.dni() == null || m.dni().contains(criteria.dni()))
            .filter(m -> criteria.name() == null || m.name().toLowerCase().contains(criteria.name().toLowerCase()))
            .filter(m -> criteria.type() == null || criteria.type().isInstance(m))
            .collect(Collectors.toList());
    }
}
