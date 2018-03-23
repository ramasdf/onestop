import React from 'react'
import PropTypes from 'prop-types'
import FooterLink from './FooterLink'
import github from 'fa/github.svg'

const styleFooter = {
  width: '100%',
  color: 'white',
  textAlign: 'center',
}

const styleFooterList = {
  listStyleType: 'none',
  lineHeight: '2em',
  fontWeight: '400',
  fontSize: '1.1em',
  display: 'inline-block',
  padding: '0 0 1em 0',
  borderBottom: '1px solid white',
}

const styleFooterItem = {
  display: 'inline-block',
  margin: 0,
}

const styleLinkNotLast = {
  borderRight: '1px solid white',
}

const styleLinkLast = {
  border: 'none',
}

const styleDetails = {
  marginBottom: '1.618em',
}

const styleVersionInfo = {
  display: 'inline',
}

const styleImageAttribution = {
  display: 'inline',
}

const styleIcon = {
  position: 'relative',
  top: '0.1em',
  maxHeight: '1em',
  maxWidth: '1em',
}

const links = [
  {
    href: '//www.ncdc.noaa.gov/about-ncdc/privacy',
    text: 'Privacy Policy',
  },
  {
    href: 'http://www.noaa.gov/foia-freedom-of-information-act',
    text: 'Freedom of Information Act',
  },
  {
    href: 'http://www.cio.noaa.gov/services_programs/info_quality.html',
    text: 'Information Quality',
  },
  {
    href: 'http://www.noaa.gov/disclaimer.html',
    text: 'Disclaimer',
  },
  {
    href: '//www.ncdc.noaa.gov/survey',
    text: 'Take Our Survey',
  },
  {
    href: 'mailto:noaa.data.catalog@noaa.gov?Subject=NOAA%20OneStop%20Feedback',
    text: 'Contact Us',
  },
  {
    href: '//www.commerce.gov/',
    text: 'Department of Commerce',
  },
  {
    href: 'http://www.noaa.gov/',
    text: 'NOAA',
  },
  {
    href: '//www.nesdis.noaa.gov/',
    text: 'NESDIS',
  },
]

class Footer extends React.Component {
  constructor(props) {
    super(props)
  }

  render() {
    const linkElements = links.map((link, i) => {
      0
      const styleLink =
        i === links.length - 1 ? styleLinkLast : styleLinkNotLast
      return (
        <li style={styleFooterItem} key={i}>
          <FooterLink href={link.href} title={link.text} style={styleLink}>
            {link.text}{' '}
          </FooterLink>
        </li>
      )
    })

    return (
      <nav aria-label="Footer">
        <div style={styleFooter}>
          <div>
            <ul style={styleFooterList}>{linkElements}</ul>
          </div>
          <div style={styleDetails}>
            <div style={styleVersionInfo}>
              <FooterLink
                href={'https://github.com/cedardevs/onestop/releases'}
                target={'_blank'}
              >
                Version: {this.props.version}{' '}
                <img
                  src={github}
                  alt="github releases"
                  style={styleIcon}
                  aria-hidden="true"
                />
              </FooterLink>
            </div>
            {' | '}
            <div style={styleImageAttribution}>
              <FooterLink
                href={
                  'https://www.toptal.com/designers/subtlepatterns/topography/'
                }
                target={'_blank'}
                title={
                  "Background image, 'Topography', made by Shankar Ganesh, CC BY-SA 3.0 - Subtle Patterns © Toptal Designers"
                }
              >
                Image Attribution
              </FooterLink>
            </div>
          </div>
        </div>
      </nav>
    )
  }
}

Footer.propTypes = {
  version: PropTypes.string.isRequired,
}

Footer.defaultProps = {
  version: '',
}

export default Footer
