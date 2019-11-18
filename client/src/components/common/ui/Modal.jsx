import React, {useRef, useState, useEffect} from 'react'
import {consolidateStyles} from '../../../utils/styleUtils'
import ResizeObserver from 'resize-observer-polyfill'
import AnimateHeight from 'react-animate-height'

const ANIMATION_DURATION = 200

export const ModalContext = () => {
  return React.createContext({})
}

export function useModal(open){
  const modalRef = useRef(null)
  const relativeRef = useRef(null)
  const contentRef = useRef(null)

  const styleContent = (left, top) => {
    return {
      position: 'absolute',
      top: `${top}px`,
      left: `${left}px`,
      transition: `width ${ANIMATION_DURATION}ms`,
    }
  }

  const [ width, setWidth ] = useState(0)
  const [ visible, setVisible ] = useState(false)
  const [ style, setStyle ] = useState(styleContent(0, 0))
  const [ modalHeight, setModalHeight ] = useState(0)

  const resize = (modalElement, contentRelativeElement, contentElement) => {
    let localX = 0
    let localY = 0
    if (modalElement && contentRelativeElement && contentElement) {
      // obtain the *viewport* coordinates of the modal region, relative modal content wrapper, and modal content
      const modalRect = modalElement.getBoundingClientRect()
      const contentRelativeRect = contentRelativeElement.getBoundingClientRect()
      const contentRect = contentElement.getBoundingClientRect()

      // now that our modal region position and modal content's position are in the same *viewport* coordinate system,
      // the difference translates the modal region into the coordinates relative to wherever the modal content actually
      // is (to preserve focus order without issue)
      localX = modalRect.left - contentRelativeRect.left
      localY = modalRect.top - contentRelativeRect.top

      // account for border of content (as `.getBoundingClientRect()` values are relative to outer-most boundary)
      const contentCSS = window.getComputedStyle(contentElement)
      const radix = 10

      // x-axis considerations
      const borderLeftWidth = parseInt(contentCSS.borderLeftWidth, radix)
      const borderRightWidth = parseInt(contentCSS.borderRightWidth, radix)
      const marginLeft = parseInt(contentCSS.marginLeft, radix)
      const marginRight = parseInt(contentCSS.marginRight, radix)
      const paddingLeft = parseInt(contentCSS.paddingLeft, radix)
      const paddingRight = parseInt(contentCSS.paddingRight, radix)

      // subtract left and right (margin+borderWidth+padding) to get effective width that won't overflow modal region
      setWidth(
        -(marginLeft + borderLeftWidth + paddingLeft) +
          modalRect.width -
          (paddingRight + borderRightWidth + marginRight)
      )

      // y-axis considerations
      // const borderTopWidth = parseInt(contentCSS.borderTopWidth, radix)
      // const borderBottomWidth = parseInt(contentCSS.borderBottomWidth, radix)
      // const marginTop = parseInt(contentCSS.marginTop, radix)
      // const marginBottom = parseInt(contentCSS.marginBottom, radix)
      // const paddingTop = parseInt(contentCSS.paddingTop, radix)
      // const paddingBottom = parseInt(contentCSS.paddingBottom, radix)

      console.log(
        `Translating position of modal region ${JSON.stringify(modalRect)}`
      )
      console.log(
        `Relative to modal content: {left: ${localX}, top: ${localY}}`
      )
      console.log(
        `Width (accounting for margin, border, and padding): ${width}px`
      )

      setStyle(styleContent(localX, localY))
      setModalHeight(contentRect.height)
    }
  }

  const [ ro ] = useState(
    () =>
      new ResizeObserver(([ entry ]) => {
        // calculate new style for modal content when modal region has resized
        resize(modalRef.current, relativeRef.current, contentRef.current)
      })
  )

  // observe resize changes to the modal region
  useEffect(() => {
    if (modalRef.current) ro.observe(modalRef.current)
    return () => ro.disconnect()
  }, [])

  return {
    modalRef,
    relativeRef,
    contentRef,
    style,
    modalHeight,
    open,
    visible,
    setVisible,
    width,
  }
}

const styleModal = {
  padding: 0,
  margin: 0,
  border: 0,
}

const Modal = props => {
  const {context} = props

  const handleAnimationStart = modal => {
    if (modal.setVisible && !modal.open) {
      modal.setVisible(false)
    }
  }

  const handleAnimationEnd = modal => {
    if (modal.setVisible && modal.open) {
      modal.setVisible(true)
    }
  }

  if (context) {
    return (
      <context.Consumer>
        {modal => (
          <AnimateHeight
            duration={ANIMATION_DURATION}
            height={modal.open ? 'auto' : 0}
            style={styleModal}
            onAnimationStart={() => handleAnimationStart(modal)}
            onAnimationEnd={() => handleAnimationEnd(modal)}
          >
            <div
              style={{
                width: '100%',
                height: modal.modalHeight ? modal.modalHeight : '100%',
              }}
              ref={modal.modalRef}
            />
          </AnimateHeight>
        )}
      </context.Consumer>
    )
  }
}
export default Modal

const styleModalRelative = {
  position: 'relative',
  top: '0',
  zIndex: 3,
}
export const ModalContent = props => {
  const {context} = props
  if (context) {
    return (
      <context.Consumer>
        {modal => (
          <div style={styleModalRelative} ref={modal.relativeRef}>
            <div
              style={consolidateStyles(props.style, {
                ...modal.style,
                // `modal.visible` is true when the height animation of the modal region is fully open; otherwise, false
                width: modal.visible ? modal.width : 0,
                // Animating width has visual side-effects with overflow and wrapping, so we must
                // prevent these effects while the modal is not completely visible
                overflow: modal.visible ? 'initial' : 'hidden',
                whiteSpace: modal.visible ? 'initial' : 'nowrap',
              })}
              ref={modal.contentRef}
            >
              {props.children}
            </div>
          </div>
        )}
      </context.Consumer>
    )
  }
}
