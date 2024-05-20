// Get the current page URL
const currentPage = window.location.pathname;

// Select all nav links
const navLinks = document.querySelectorAll('.nav-item .nav-link');

navLinks.forEach(link => {
    // Check if the link's href attribute matches the current page
    if (link.getAttribute('href') === currentPage) {
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

$("#page-select").change(function () {
    extracted.call(this);
});

$("#page-sort").change(function () {
    extracted.call(this);
});

function extracted() {
    const value = $(this).val();

    const currentUrl = new URL(window.location.href);

    const searchParams = currentUrl.searchParams;
    if (currentUrl.pathname.includes('keyword')) {
        searchParams.set('category', category);
        searchParams.set('inputCategory', inputCategory);
    }

    if (isNaN(value)) {
        searchParams.set('pageSize', pageSize);
        searchParams.set('sort', value);
    } else {
        searchParams.set('pageSize', value);
        searchParams.set('sort', sort);
    }

    window.location.href = currentUrl.pathname + '?' + searchParams.toString();
}

function FunTbodyLoadingBarStart() {
    var tbody = $('#tbody-content'); // Target element
    var position = tbody.offset(); // Get the position of the tbody element
    var height = tbody.outerHeight(); // Get the outer height of the tbody element
    var width = tbody.outerWidth(); // Get the outer width of the tbody element

    var backGroundCover = "<div id='back'></div>"; // Overlay cover
    var loadingBarImage = "<div id='loadingBar'><img src='/img/loading.gif' height='30' width='30' /></div>";

    $('body').append(backGroundCover).append(loadingBarImage); // Append to body

    $('#back').css({
        'position': 'absolute',
        'top': position.top,
        'left': position.left,
        'width': width,
        'height': height,
        'background-color': 'rgba(0,0,0,0.5)',
        'z-index': '10000',
        'pointer-events': 'none'
    });

    $('#loadingBar').css({
        'position': 'absolute',
        'top': position.top + height / 2 - 50, // Centering the loading gif vertically
        'left': position.left + width / 2 - 50, // Centering the loading gif horizontally
        'z-index': '10001'
    });

    $('#back').show();
    $('#loadingBar').show();
}

function FunTbodyLoadingBarEnd() {
    $('#back, #loadingBar').hide().remove();
}

