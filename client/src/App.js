import React from 'react'
import {Provider} from 'react-redux'
import {Route, Switch} from 'react-router'
import {ConnectedRouter} from 'connected-react-router'
import RootContainer from './components/root/RootContainer'
import GoogleAnalyticsContainer from './components/analytics/GoogleAnalyticsContainer'
import {ROUTE, shouldFocusMain} from './utils/urlUtils'
import _ from 'lodash'

// this higher-order component is kept separate from index.jsx
// to parameterize the store and history for tests
const isTest = process.env.NODE_ENV === 'test'

const App = (store, history) => {
  const reload = () => window.location.reload()
  if (history.location.hash) {
    // Redirect URL from old hash-based routing to new path
    // This will preserve bookmarks and any external links that have been made
    const hash = history.location.hash.replace('#', '')
    const parts = _.split(hash, '?')
    const locationDescriptor = {
      pathname: parts[0],
      search: parts[1] ? `?${parts[1]}` : '',
    }
    shouldFocusMain(history.location, locationDescriptor)
    history.push(locationDescriptor)
  }
  return (
    <Provider store={store}>
      <ConnectedRouter history={history}>
        <div>
          {isTest ? null : <GoogleAnalyticsContainer />}
          <Switch>
            <Route path={ROUTE.sitemap.path} exact onEnter={reload} />
            <RootContainer />
          </Switch>
        </div>
      </ConnectedRouter>
    </Provider>
  )
}

export default App
