// Get the current page URL
const currentPage = window.location.pathname;

// Select all nav links
const navLinks = document.querySelectorAll('.nav-item .nav-link');

navLinks.forEach(link => {
  // Check if the link's href attribute matches the current page
  if(link.getAttribute('href') === currentPage) {
    // Add 'active' class
    link.classList.add('active');
  } else {
    // Remove 'active' class
    link.classList.remove('active');
  }
});

function FunLoadingBarStart() {
  var backHeight = $(document).height(); // 뒷 배경의 상하 폭
  var backWidth = window.screen.width; // 뒷 배경의 좌우 폭
  var backGroundCover = "<div id='back'></div>"; // 뒷 배경을 감쌀 커버
  var loadingBarImage = ''; // 가운데 띄워 줄 이미지
  loadingBarImage += "<div id='loadingBar'>";
  loadingBarImage += "     <img src='/img/loading.gif' height='100' width='100' />"; // 로딩 바 이미지
  loadingBarImage += "</div>";

  $('body').append(backGroundCover).append(loadingBarImage);

  $('#back').css({
    'position': 'fixed', // Fixed position to cover entire screen
    'top': '0',
    'left': '0',
    'right': '0',
    'bottom': '0',
    'width': '100%', // Ensure it covers full width of the viewport
    'height': '100%', // Ensure it covers full height of the viewport
    'background-color': 'black', // Optional: Change the background color
    'opacity': '0.3',
    'z-index': '10000', // High z-index to be on top of other elements
    'pointer-events': 'auto' // Capture pointer events
  });

  $('#loadingBar').css({
    'position': 'fixed',
    'top': '50%',
    'left': '50%',
    'transform': 'translate(-50%, -50%)',
    'z-index': '10001' // Ensure loading image is above the overlay
  });

  $('#back').show();
  $('#loadingBar').show();
}


function FunLoadingBarEnd() {
  $('#back, #loadingBar').hide();
  $('#back, #loadingBar').remove();
}