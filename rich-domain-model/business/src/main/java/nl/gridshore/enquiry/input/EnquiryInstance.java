/*
 * Copyright (c) 2008 JTeam B.V.
 * www.jteam.nl
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JTeam B.V. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you
 * entered into with JTeam.
 */
package nl.gridshore.enquiry.input;

import nl.gridshore.enquiry.def.EnquiryDef;
import nl.gridshore.enquiry.def.QuestionDef;
import nl.gridshore.rdm.persistence.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class EnquiryInstance extends BaseEntity {

    @ManyToOne(optional = false)
    private EnquiryDef enquiryDef;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "enquiryInstance")
    private List<AnswerInstance> answerInstances = new ArrayList<AnswerInstance>();

    private transient Map<QuestionDef, AnswerInstance> answerMap = new HashMap<QuestionDef, AnswerInstance>();

    protected EnquiryInstance() {
    }

    public EnquiryInstance(final EnquiryDef enquiryDef) {
        this.enquiryDef = enquiryDef;
    }

    public List<AnswerInstance> getAnswerInstances() {
        return Collections.unmodifiableList(answerInstances);
    }

    public EnquiryDef getEnquiryDef() {
        return enquiryDef;
    }

    public void addAnswer(final AnswerInstance answerInstance) {
        if (!getEnquiryDef().equals(answerInstance.getEnquiryDef())) {
            throw new EnquiryException("The answer belongs to another enquiry than this instance");
        }
        if (getAnswerForQuestion(answerInstance.getQuestionDef()) != null) {
            throw new EnquiryException("This answer answers a question that is already answered");
        }
        answerInstances.add(answerInstance);
        answerInstance.setEnquiryInstance(this);
        answerMap.put(answerInstance.getQuestionDef(), answerInstance);
    }

    public AnswerInstance getAnswerForQuestion(QuestionDef questionDef) {
        return answerMap.get(questionDef);
    }

    @PostLoad
    protected void populateAnswerMap() {
        answerMap.clear();
        for (AnswerInstance answer : answerInstances) {
            answerMap.put(answer.getQuestionDef(), answer);
        }
    }
}
