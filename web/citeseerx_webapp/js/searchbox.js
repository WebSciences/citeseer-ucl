function startSearchTabs(activeTab) {
 /* search_tabs */
 var searchTabs = new mootabs('search_tabs', {
  width:'405px',
  height:'250px',
  changeTransition: 'none',
  mouseOverClass:'over'
 });

 /* Set the focus on the search field when the tab is clicked */
 $$('#tabs_titles li').filterByAttribute('title', '=', 'docs_tab')[0].addEvent('click', function(event) {
  var docSearchBox = $$('#docs_tab form input').filterByAttribute('name', '=', 'q')[0];
  
  // If the auth search box has text bring it here.
  var authSearchBox = $$('#auth_tab form input').filterByAttribute('name', '=', 'q')[0];
  if (authSearchBox.getProperty('value') != '') {
    docSearchBox.setProperty('value', authSearchBox.getProperty('value'));
    authSearchBox.setProperty('value', '');
  }
  
  // Set focus on input field
  docSearchBox.focus();
 });

 $$('#tabs_titles li').filterByAttribute('title', '=', 'auth_tab')[0].addEvent('click', function(event) {
  var authSearchBox = $$('#auth_tab form input').filterByAttribute('name', '=', 'q')[0];
  
  // If the doc search box has text bring it here.
  var docSearchBox = $$('#docs_tab form input').filterByAttribute('name', '=', 'q')[0];

  if (docSearchBox.getProperty('value') != '') {
    authSearchBox.setProperty('value', docSearchBox.getProperty('value'));
    docSearchBox.value = '';
  }
  
  // Set focus on input field
  authSearchBox.focus();
 });
 
 // Set defaul tab.
 searchTabs.activate(activeTab);
}