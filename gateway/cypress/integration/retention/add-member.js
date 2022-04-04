/// <reference types="cypress" />
import data from '../../fixtures/test-data.json';
import admin from '../../fixtures/admin-data.json';
const { salesforceId, clientName, clientId } = data.member;

describe('Test "Add member" functionality', () => {
  it('Add member', function() {
    cy.programmaticSignin(admin.email, admin.password);
    cy.visit('/ms-member/new');
    // Check required field flags
    cy.get('#field_salesforceId').should('have.class', 'ng-invalid');
    cy.get('#field_clientName').should('have.class', 'ng-invalid');
    // Save button should be disabled
    cy.get('#save-entity')
      .invoke('attr', 'disabled')
      .should('exist');
    // Check salesforce id warning message when field is clear
    cy.get('#field_salesforceId')
      .type(salesforceId)
      .clear();
    cy.get('small').should('exist');
    cy.get('#save-entity')
      .invoke('attr', 'disabled')
      .should('exist');
    // Enter existing salesforce id to generate an error
    cy.get('#field_salesforceId').type(salesforceId);
    // Enter invalid client name to generate an error
    cy.get('#field_clientName')
      .type(data.invalidString)
      .clear();
    cy.get('small').should('exist');
    cy.get('#save-entity')
      .invoke('attr', 'disabled')
      .should('exist');
    cy.get('#field_clientName').type(clientName);
    cy.get('#save-entity').click();
    // Two error messages should appear for existing salesforce id and member name
    cy.get('.validation-errors')
      .children()
      .should('have.length', 2);
    // Enter invalid client id to generate an error
    cy.get('#field_clientId').type(data.invalidString);
    cy.get('small').should('exist');
    // Check for flag on client id input field
    cy.get('#field_clientId').should('have.class', 'ng-invalid');
    cy.get('#save-entity')
      .invoke('attr', 'disabled')
      .should('exist');
    // Check the enable assertions checkbox
    cy.get('#field_assertionServiceEnabled').check();
    cy.get('#field_clientId').clear();
    cy.get('#field_clientId').type(clientId);
    // Checkbox should be unchecked after clearing client id field
    cy.get('#field_assertionServiceEnabled')
      .should('not.be.checked')
      .check();
    // Save button should be clickable
    cy.get('#save-entity')
      .invoke('attr', 'disabled')
      .should('not.exist');
  });
});
