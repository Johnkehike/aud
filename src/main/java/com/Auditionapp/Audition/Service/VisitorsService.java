package com.Auditionapp.Audition.Service;

import com.Auditionapp.Audition.Entity.Visitors;

public interface VisitorsService {
    void saveNewVisitor(Visitors visitors);

    Visitors findId(String visitorId);

    void updateVisitor(String entryStatus, String visitorId);
}
