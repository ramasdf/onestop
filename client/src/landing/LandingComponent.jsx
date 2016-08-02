import React from 'react'
import ReactDOM from 'react-dom'
import TextSearchField from '../search/TextSearchFieldComponent'
import TemporalContainer from '../search/temporal/TemporalContainer'
import MapContainer from '../search/map/MapContainer'
import ToggleDisplay from 'react-toggle-display'
import styles from './landing.css'


class LandingComponent extends React.Component {
  constructor(props) {
    super(props)
    this.submit = props.submit
    this.updateQuery = props.updateQuery
    this.toggleMap = this.toggleMap.bind(this)
    this.handleClick = this.handleClick.bind(this)
    this.state = {
      showMap: false
    }
  }

  handleClick(e) {
    // Close map when user clicks outside of it
    var component = ReactDOM.findDOMNode(this.refs.mapComponent)
    if (this.state.showMap && !component.contains(e.target) && e.srcElement.id !== 'mapButton') {
      this.toggleMap()
    }
  }

  componentWillMount() {
    document.addEventListener('click', this.handleClick, false);
  }

  componentWillUnmount() {
    document.removeEventListener('click', this.handleClick, false);
  }

  toggleMap() {
    this.state.showMap = !this.state.showMap
    this.forceUpdate()
  }

  onClickOut(e) {
    if (this.state.showMap) {
      this.state.showMap = false //Close map when clicked anywhere outside of it
      this.forceUpdate()
    }
  }

  render() {
    return <div>

      </div>
  }
}

export default LandingComponent
