/// <reference types="cypress" />
import data from '../../fixtures/test-data.json';
import credentials from '../../fixtures/credentials.json';
const { salesforceId, clientName, clientId } = data.member;

describe('Manage members menu', () => {
  it('Test edit member form', function() {
    cy.programmaticSignin(credentials.adminEmail, credentials.adminPassword)
    cy.visit(`/ms-member/${data.member.id}/edit`)
    // Shouldn't be able to save without a salesforce id
    cy.get('#field_salesforceId').clear()
    cy.get('#save-entity').invoke('attr', 'disabled').should('exist')
    cy.get('small').filter('[jhitranslate="entity.validation.required.string"]').should('exist')
    cy.get('#field_salesforceId').type(salesforceId)
    // Shouldn't be able to save without a client name
    cy.get('#field_clientName').clear()
    cy.get('#save-entity').invoke('attr', 'disabled').should('exist')
    cy.get('small').filter('[jhitranslate="entity.validation.required.string"]').should('exist')
    // Enter existing client name and check for relevant error message
    cy.get('#field_clientName').type(data.populatedMember.clientName)
    cy.get('#save-entity').click()
    cy.get('.validation-errors').children().should('have.length', 1)
    cy.get('#field_clientName').clear().type(clientName)
    // Check client id warning message
    cy.get('#field_clientId').type(data.invalidString)
    cy.get('small').should('exist')
    cy.get('#save-entity').invoke('attr', 'disabled').should('exist')
    cy.get('#field_clientId').clear()    
    cy.get('#field_clientId').type(clientId)
    // Assertions enabled checkbox should be unchecked after clearing client id field
    cy.get('#field_assertionServiceEnabled').should('not.be.checked').check()
    cy.get('#save-entity').click()
  });
});